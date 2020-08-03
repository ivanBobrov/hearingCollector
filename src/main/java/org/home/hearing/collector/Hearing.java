package org.home.hearing.collector;

import java.time.LocalDateTime;

public class Hearing {
    private final String id;
    private final String time;
    private final String info;
    private final String room;
    private final String judge;
    private final String result;
    private final String law;
    private final String courtName;
    private final String type;
    private final LocalDateTime dateTime;

    private Hearing(Builder builder) {
        this.id = builder.id;
        this.time = builder.time;
        this.info = builder.info;
        this.room = builder.room;
        this.judge = builder.judge;
        this.result = builder.result;
        this.law = builder.law;
        this.courtName = builder.courtName;
        this.type = builder.type;
        this.dateTime = builder.dateTime;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getInfo() {
        return info;
    }

    public String getRoom() {
        return room;
    }

    public String getJudge() {
        return judge;
    }

    public String getResult() {
        return result;
    }

    public String getLaw() {
        return law;
    }

    public String getCourtName() {
        return courtName;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Hearing{" +
                "id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", info='" + info + '\'' +
                ", room='" + room + '\'' +
                ", judge='" + judge + '\'' +
                ", result='" + result + '\'' +
                ", law='" + law + '\'' +
                ", courtName='" + courtName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = "";
        private String time = "";
        private String info = "";
        private String room = "";
        private String judge = "";
        private String result = "";
        private String law = "";
        private String courtName = "";
        private String type = "";
        private LocalDateTime dateTime;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTime(String time) {
            this.time = time;
            return this;
        }

        public Builder setInfo(String info) {
            this.info = info;
            return this;
        }

        public Builder setRoom(String room) {
            this.room = room;
            return this;
        }

        public Builder setJudge(String judge) {
            this.judge = judge;
            return this;
        }

        public Builder setResult(String result) {
            this.result = result;
            return this;
        }

        public Builder setLaw(String law) {
            this.law = law;
            return this;
        }

        public Builder setCourtName(String courtName) {
            this.courtName = courtName;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Hearing build() {
            return new Hearing(this);
        }
    }
}
