package com.waynlaw.albabot.strategist.runner

import com.waynlaw.albabot.strategist.model.Action

trait Actor {
    def run(action: Action.ActionVal): Unit
}
