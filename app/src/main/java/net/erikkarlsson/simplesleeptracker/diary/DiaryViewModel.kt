package net.erikkarlsson.simplesleeptracker.diary

import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import javax.inject.Inject

class DiaryViewModel @Inject constructor(diaryComponent: DiaryComponent) :
        ElmViewModel<DiaryState, DiaryMsg, DiaryCmd>(diaryComponent)