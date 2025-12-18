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

            private ItemsWrapper items;

            private int pageNo;
            private int numOfRows;
            private int totalCount;

            @Data
            public static class ItemsWrapper {
                private List<KmaItem> item;
            }
        }
    }
}