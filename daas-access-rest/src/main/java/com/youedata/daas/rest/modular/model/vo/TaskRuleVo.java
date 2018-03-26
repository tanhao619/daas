package com.youedata.daas.rest.modular.model.vo;

import java.util.List;

/**
 * 数据单元创建接口传入数据body实体
 */
public class TaskRuleVo {
    private Structure structure;
    private Dunit dunit;
    private Option option;

    class Structure {
        private List<Cloumn> cloumn;

        public List<Cloumn> getCloumn() {
            return cloumn;
        }

        public void setCloumn(List<Cloumn> cloumn) {
            this.cloumn = cloumn;
        }

        class Cloumn {
            private String field;//字段名
            private String primary;//是否主键
            private String type;//字段类型
            private String length;//字段长度
            private String defaultValue;//默认值
            private String notNull;//是否为空
            private String description;//字段描述

            public String getField() {
                return field;
            }

            public void setField(String field) {
                this.field = field;
            }

            public String getPrimary() {
                return primary;
            }

            public void setPrimary(String primary) {
                this.primary = primary;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getLength() {
                return length;
            }

            public void setLength(String length) {
                this.length = length;
            }

            public String getDefaultValue() {
                return defaultValue;
            }

            public void setDefaultValue(String defaultValue) {
                this.defaultValue = defaultValue;
            }

            public String getNotNull() {
                return notNull;
            }

            public void setNotNull(String notNull) {
                this.notNull = notNull;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }

    class Dunit {
        private String engine;  // 存储引擎类型：MYSQL、HDFS
        private String type;    // 存储类型：TABLE、FILE

        public String getEngine() {
            return engine;
        }

        public void setEngine(String engine) {
            this.engine = engine;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    class Option {
        private boolean overwrite;

        public boolean isOverwrite() {
            return overwrite;
        }

        public void setOverwrite(boolean overwrite) {
            this.overwrite = overwrite;
        }
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Dunit getDunit() {
        return dunit;
    }

    public void setDunit(Dunit dunit) {
        this.dunit = dunit;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }
}
