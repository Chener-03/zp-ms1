package xyz.chener.zp.task.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn


@RestController
@RequestMapping("/api/web/task")
@UnifiedReturn
@Validated
class TaskListController {
}