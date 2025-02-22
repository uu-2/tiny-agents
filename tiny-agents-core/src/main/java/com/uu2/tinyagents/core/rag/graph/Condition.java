package com.uu2.tinyagents.core.rag.graph;

import lombok.NoArgsConstructor;

public interface Condition {
    String cond();

    static Condition Any(String any) {
        return new AnyCond(any);
    }

    Object encode();
    void decode(Object content);

    @NoArgsConstructor
    class AnyCond implements Condition {
        private String any;

        public AnyCond(String any) {
            this.any = any;
        }

        @Override
        public String cond() {
            return any;
        }

        @Override
        public Object encode() {
            return any;
        }

        @Override
        public void decode(Object content) {
            this.any = (String) content;
        }
    }
}
