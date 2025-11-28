package com.websever.websever.dto.response;

import com.websever.websever.dto.KmaItem;
import lombok.Data;

import java.util.List;

@Data
public class KmaResponse {
    private KmaResponseBody response;

    @Data
    public static class KmaResponseBody {
        private Header header;
        private Body body;

        @Data
        public static class Header {
            private String resultCode;
            private String resultMsg;
        }

        @Data
        public static class Body {
            private String dataType;

            // KmaItems 클래스를 Body 내부에 ItemsWrapper로 정의하여 사용합니다. (외부 파일 제거)
            private ItemsWrapper items;

            private int pageNo;
            private int numOfRows;
            private int totalCount;

            // KmaItems.java의 내용을 여기에 병합했습니다. (JSON 필드명 'item'을 유지)
            @Data
            public static class ItemsWrapper {
                private List<KmaItem> item;
            }
        }
    }
}