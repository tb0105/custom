package com.rzq.custom.base;

/**
     * action
     */
    public class BaseAction {
        private String action;
        private String category;

        public BaseAction(String action, String category) {
            this.action = action;
            this.category = category;
        }

        public String getAction() {
            return action;
        }

        public String getCategory() {
            return category;
        }
    }