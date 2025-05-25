package com.ssafy.htrip.recommendation.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.htrip.attraction.dto.AttractionDto;
import com.ssafy.htrip.attraction.repository.AttractionRepository;
import com.ssafy.htrip.attraction.service.AreaService;
import com.ssafy.htrip.attraction.service.AttractionService;
import com.ssafy.htrip.plan.dto.PlanDto;
import com.ssafy.htrip.plan.dto.PlanDayDto;
import com.ssafy.htrip.plan.dto.PlanItemDto;
import com.ssafy.htrip.recommendation.dto.AIRecommendationRequest;
import com.ssafy.htrip.recommendation.dto.AIRecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * 라우터 스테이트 방식에 최적화된 추천 서비스
 * - 임시 저장소를 사용하지 않고 stateless 서비스 제공
 * - 모든 상태는 프론트엔드의 라우터 상태에서 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIRecommendationServiceImpl implements AIRecommendationService {

    private final AttractionRepository attractionRepository;
    private final AttractionService attractionService;
    private final ChatClient chatClient;
    private final AreaService areaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 여행 추천 계획 생성
     * - 임시 저장소를 사용하지 않고 결과만 반환
     */
    @Override
    public AIRecommendationResponse generateRecommendationPlan(AIRecommendationRequest request) {
        log.info("AI 여행 추천 시작: {}", request);

        // 1. DB에서 해당 지역의 추천할만한 관광지 필터링
        List<AttractionDto> attractions = filterAttractions(request);

        if (attractions.isEmpty()) {
            throw new IllegalArgumentException("선택한 조건에 맞는 여행지가 없습니다. 조건을 변경해 주세요.");
        }

        log.info("필터링된 관광지 수: {}", attractions.size());

        // 2. LLM을 통해 여행 경로 생성
        PlanDto recommendedPlan = generateTravelPlan(attractions, request);

        // 추천 ID 생성 (UUID)
        String recommendationId = UUID.randomUUID().toString();

        // 3. 결과 반환 (임시 저장 없이)
        AIRecommendationResponse response = AIRecommendationResponse.builder()
                .recommendationId(recommendationId)
                .recommendedPlan(recommendedPlan)
                .reasoning("AI가 분석한 결과, 선택하신 조건에 가장 적합한 여행 코스입니다.")
                .build();

        log.info("AI 여행 추천 완료: {}", recommendationId);
        return response;
    }

    private List<AttractionDto> filterAttractions(AIRecommendationRequest request) {
        // 기본 필터링 조건
        List<AttractionDto> filteredAttractions = new ArrayList<>();

        // 기본 카테고리 필터 생성
        Map<String, List<String>> styleToCategories = createStyleToCategoryMap();

        // 선택한 여행 스타일에 따른 카테고리 추출
        Set<String> targetCategories = new HashSet<>();
        for (String style : request.getTravelStyles()) {
            if (styleToCategories.containsKey(style)) {
                targetCategories.addAll(styleToCategories.get(style));
            }
        }

        // 지역 코드 기반 필터링
        attractionRepository.findByAreaCode(
                request.getAreaCode(),
                PageRequest.of(0, 50)
        ).forEach(attraction -> {
            try {
                AttractionDto dto = attractionService.findById(attraction.getPlaceId());

                // 카테고리 필터링 (여행 스타일)
                if (targetCategories.isEmpty() ||
                        matchesAnyCategory(dto, targetCategories)) {
                    filteredAttractions.add(dto);
                }
            } catch (Exception e) {
                log.error("Failed to convert attraction", e);
            }
        });

        return filteredAttractions;
    }

    private boolean matchesAnyCategory(AttractionDto attraction, Set<String> targetCategories) {
        // 카테고리 확인 (category1, category2, category3 중 하나라도 일치하면 됨)
        return targetCategories.contains(attraction.getCategory1()) ||
                targetCategories.contains(attraction.getCategory2()) ||
                targetCategories.contains(attraction.getCategory3());
    }

    private Map<String, List<String>> createStyleToCategoryMap() {
        Map<String, List<String>> styleToCategories = new HashMap<>();

        // 자연/풍경 (A01: 자연)
        styleToCategories.put("자연/풍경", Arrays.asList("A01"));

        // 맛집/음식 (A05: 음식)
        styleToCategories.put("맛집/음식", Arrays.asList("A05","C0117"));

        // 역사/문화 (A02: 인문(문화/예술/역사))
        styleToCategories.put("역사/문화", Arrays.asList("A02"));

        // 쇼핑 (A04: 쇼핑)
        styleToCategories.put("쇼핑", Arrays.asList("A04"));

        // 휴양/힐링 (A02: 인문 > A0202: 휴양관광지)
        styleToCategories.put("휴양/힐링", Arrays.asList("C0114", "A0202"));

        // 액티비티 (A03: 레포츠)
        styleToCategories.put("액티비티", Arrays.asList("A03"));

        // 사진촬영 (주로 자연 및 건축/조형물)
        styleToCategories.put("사진촬영", Arrays.asList("A01", "A0201", "A0205"));

        // 예술/공연 (A02: 인문 > A0206: 문화시설, A0208: 공연/행사)
        styleToCategories.put("예술/공연", Arrays.asList("A0206", "A0207", "A0208"));

        // 캠핑 (A03: 레포츠 > A0302: 육상 레포츠)
        styleToCategories.put("캠핑", Arrays.asList("C0116"));

        // 테마파크 (A02: 인문 > A0202: 휴양관광지)
        styleToCategories.put("테마파크", Arrays.asList("A02", "A0202"));

        return styleToCategories;
    }

    private PlanDto generateTravelPlan(List<AttractionDto> attractions, AIRecommendationRequest request) {
        // 지역 정보 준비
        String regionName = areaService.getAreaById(request.getAreaCode()).getName();

        // 1. LLM에 전달할 시스템 프롬프트 준비
        String systemPrompt = """
        당신은 한국 여행 전문가로서 최적의 여행 코스를 짜주는 도우미입니다.
        
        다음 조건에 맞게 여행 코스를 짜주세요:
        1. 제공된 관광지 목록에서만 선택해야 합니다.
        2. 여행 일수에 맞게 하루 3~5개 정도의 관광지를 선택해 일정을 구성합니다.
        3. 이동 거리와 동선을 고려해 효율적인 여행 코스를 짜야 합니다.
        4. 선택한 여행 스타일과 동행자에 맞는 관광지를 우선 선택합니다.
        5. 여행 일정 유형(빡빡/널널)에 맞게 일정의 밀도를 조절합니다.
        
        여행 일정을 구성할 때 단순히 목록의 순서대로가 아닌, 다음 기준을 고려하세요:
        - 지리적 위치와 이동 거리 최적화
        - 각 관광지의 운영 시간
        - 비슷한 유형의 관광지가 연속되지 않도록 다양성 유지
        - 일정 흐름상 자연스러운 순서 (예: 아침에 활동적인 장소, 저녁에 여유로운 장소)
        
        출력 형식은 반드시 다음 JSON 형식을 지켜주세요:
        ```json
        {
          "title": "여행 제목",
          "days": [
            {
              "dayDate": 1,
              "items": [
                {
                  "placeId": 123,
                  "sequence": 1,
                  "memo": "방문 추천 이유"
                },
                ...
              ]
            },
            ...
          ]
        }
        ```
        
        반드시 주어진 관광지 목록의 placeId만 사용하고, JSON 형식을 정확히 지켜주세요.
        """;

        // 2. 사용자 메시지 준비 (선택 조건 + 관광지 목록)
        StringBuilder userMessageBuilder = new StringBuilder();
        userMessageBuilder.append("여행 조건:\\n");
        userMessageBuilder.append("- 지역: ").append(regionName).append("\\n");
        userMessageBuilder.append("- 기간: ").append(request.getDurationDays()).append("일\\n");
        userMessageBuilder.append("- 동행자: ").append(request.getTravelWith()).append("\\n");
        userMessageBuilder.append("- 여행 스타일: ").append(String.join(", ", request.getTravelStyles())).append("\\n");
        userMessageBuilder.append("- 일정 유형: ").append(request.getScheduleType()).append("\\n\\n");

        userMessageBuilder.append("사용 가능한 관광지 목록:\\n");
// 각 관광지의 위도/경도 정보를 더 명확하게 제공
        for (AttractionDto attraction : attractions) {
            userMessageBuilder.append("- ID: ").append(attraction.getPlaceId())
                    .append(", 이름: ").append(attraction.getTitle())
                    .append(", 주소: ").append(attraction.getAddress1())
                    .append(", 카테고리: ").append(attraction.getCategory1());

            // 위도/경도 정보를 항상 포함시키고 더 강조
            double lat = attraction.getLatitude() != null ? attraction.getLatitude() : 0;
            double lng = attraction.getLongitude() != null ? attraction.getLongitude() : 0;
            userMessageBuilder.append(", 위치좌표: [").append(lat).append(", ").append(lng).append("]");

            userMessageBuilder.append("\\n");
        }

        // 3. LLM 호출
        UserMessage m = new UserMessage(userMessageBuilder.toString());
        String prompt = systemPrompt + "\n\n" + userMessageBuilder;
        log.info("AI 프롬프트 전송 중...");
        String response = chatClient.prompt(prompt).system(t->t.param("language","korean")).call().content();
        log.info("AI 응답 수신 완료");

        // 4. JSON 파싱 및 PlanDto 변환
        try {
            // LLM 응답에서 JSON 부분 추출
            String jsonContent = extractJson(Objects.requireNonNull(response));
            log.info("추출된 JSON: {}", jsonContent);

            // JSON을 PlanDto로 변환
            PlanDto planDto = convertJsonToPlanDto(jsonContent, request);
            return planDto;
        } catch (Exception e) {
            log.error("Failed to parse LLM response", e);
            throw new RuntimeException("AI 여행 계획 생성에 실패했습니다: " + e.getMessage());
        }
    }

    private String extractJson(String responseContent) {
        // LLM 응답에서 JSON 부분만 추출 (```json과 ``` 사이의 내용)
        int startIndex = responseContent.indexOf("```json");
        if (startIndex == -1) {
            startIndex = responseContent.indexOf("```");
        }

        if (startIndex != -1) {
            startIndex = responseContent.indexOf("{", startIndex);
            int endIndex = responseContent.lastIndexOf("}");
            if (startIndex != -1 && endIndex != -1) {
                return responseContent.substring(startIndex, endIndex + 1);
            }
        }

        // JSON 형식을 찾지 못한 경우
        log.error("응답에서 JSON을 찾지 못함: {}", responseContent);
        throw new RuntimeException("AI 응답에서 JSON 형식을 찾을 수 없습니다.");
    }

    private PlanDto convertJsonToPlanDto(String jsonContent, AIRecommendationRequest request) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // Parse the JSON into a temporary structure
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            // Create a new PlanDto
            PlanDto planDto = new PlanDto();
            planDto.setTitle(rootNode.has("title") ?
                    rootNode.get("title").asText() :
                    request.getAreaCode() + "으로의 " + request.getDurationDays() + "일 여행");

            // Set dates
            LocalDate startDate = LocalDate.now();
            planDto.setStartDate(startDate);
            planDto.setEndDate(startDate.plusDays(request.getDurationDays() - 1));
            planDto.setPublic(true);

            // Process days and items
            if (rootNode.has("days") && rootNode.get("days").isArray()) {
                List<PlanDayDto> dayDtos = new ArrayList<>();
                JsonNode daysNode = rootNode.get("days");

                for (JsonNode dayNode : daysNode) {
                    PlanDayDto dayDto = new PlanDayDto();
                    dayDto.setDayDate(dayNode.has("dayDate") ? dayNode.get("dayDate").asInt() : 0);

                    // Process items for this day
                    if (dayNode.has("items") && dayNode.get("items").isArray()) {
                        List<PlanItemDto> itemDtos = new ArrayList<>();
                        JsonNode itemsNode = dayNode.get("items");

                        int sequence = 1;
                        for (JsonNode itemNode : itemsNode) {
                            PlanItemDto itemDto = new PlanItemDto();
                            itemDto.setPlaceId(itemNode.has("placeId") ? itemNode.get("placeId").asInt() : 0);
                            itemDto.setSequence(itemNode.has("sequence") ? itemNode.get("sequence").asInt() : sequence++);
                            itemDto.setMemo(itemNode.has("memo") ? itemNode.get("memo").asText() : "");

                            // Set default times if needed
                            if (!itemNode.has("startTime") && !itemNode.has("endTime")) {
                                LocalTime baseTime = LocalTime.of(9, 0).plusHours(2 * (sequence - 1));
                                itemDto.setStartTime(baseTime);
                                itemDto.setEndTime(baseTime.plusHours(1));
                            } else {
                                // Parse times if provided
                                if (itemNode.has("startTime")) {
                                    itemDto.setStartTime(LocalTime.parse(itemNode.get("startTime").asText()));
                                }
                                if (itemNode.has("endTime")) {
                                    itemDto.setEndTime(LocalTime.parse(itemNode.get("endTime").asText()));
                                }
                            }

                            itemDtos.add(itemDto);
                        }

                        dayDto.setItems(itemDtos);
                    }

                    dayDtos.add(dayDto);
                }

                planDto.setDays(dayDtos);
            } else {
                // Create default days if not provided
                List<PlanDayDto> defaultDays = new ArrayList<>();
                for (int i = 0; i < request.getDurationDays(); i++) {
                    PlanDayDto dayDto = new PlanDayDto();
                    dayDto.setDayDate(i + 1);
                    dayDto.setItems(new ArrayList<>());
                    defaultDays.add(dayDto);
                }
                planDto.setDays(defaultDays);
            }

            return planDto;
        } catch (Exception e) {
            log.error("Failed to parse JSON into PlanDto: {}", e.getMessage());
            log.error("JSON content: {}", jsonContent);
            throw new RuntimeException("AI 응답 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}