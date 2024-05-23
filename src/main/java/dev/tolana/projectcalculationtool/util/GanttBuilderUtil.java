package dev.tolana.projectcalculationtool.util;


//const items = new vis.DataSet([
//        {id: 1, content: "item 1", start: "2014-04-20"},
//        {id: 2, content: "item 2", start: "2014-04-14"},
//        {id: 3, content: "item 3", start: "2014-04-18"},
//[       {"id":1,"content":"Frontend Task","start":"2024-05-18"}]
//        {id: 4, content: "item 4", start: "2014-04-16", end: "2014-04-19"},
//        {id: 5, content: "item 4", start: "2014-04-16", end: "2014-04-22"},
//        {id: 6, content: "item 4", start: "2014-04-16", end: "2014-04-28"},
//        {id: 7, content: "item 5", start: "2014-04-25"},
//        {id: 8, content: "item 6", start: "2014-04-27", type: "point"}
//    ]);


import com.google.gson.Gson;
import dev.tolana.projectcalculationtool.dto.ProjectStatsDto;
import dev.tolana.projectcalculationtool.dto.ResourceEntityViewDto;
import dev.tolana.projectcalculationtool.dto.TaskStatsDto;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
public class GanttBuilderUtil {
    public class Item {
        private long id;
        private String content;
        private String start;
        private String end;

        public Item(long id, String content, String start) {
            this.id = id;
            this.content = content;
            this.start = start;
        }

        public Item(long id, String content, String start, String end) {
            this.id = id;
            this.content = content;
            this.start = start;
            this.end = end;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String buildGanttDataSet(List<Entity> tasks) {
        List<Item> items = new ArrayList<>();
        int id = 1;
        for (Entity entity : tasks) {
            Task task = (Task) entity;
            Item item;

            int days = task.getEstimatedHours()/8;
            LocalDateTime end = task.getDateCreated().plusDays(days);
            item = new Item(
                    task.getId(),
                    task.getName(),
                    formatter.format(task.getDateCreated()),
                    formatter.format(end)
            );
            items.add(item);
        }
        return new Gson().toJson(items);
    }
}
