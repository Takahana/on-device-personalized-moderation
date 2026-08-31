package com.example.mystream.data

import com.example.mystream.domain.content.StreamingContentCard
import com.example.mystream.domain.content.StreamingContentId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DataRepository {
  val cards: Flow<List<StreamingContentCard>>
}

class DefaultDataRepository : DataRepository {
  override val cards: Flow<List<StreamingContentCard>> = flow { emit(
    listOf(
      StreamingContentCard(
        id = StreamingContentId("soccer"),
        "サッカー"
      ),
      StreamingContentCard(
        id = StreamingContentId("news"),
        "政治ニュース"
      ),
      StreamingContentCard(
        id = StreamingContentId("reality"),
        "恋愛リアリティショー"
      ),
      StreamingContentCard(
        id = StreamingContentId("variety"),
        "お笑いバラエティ"
      ),
    )
  ) }
}
