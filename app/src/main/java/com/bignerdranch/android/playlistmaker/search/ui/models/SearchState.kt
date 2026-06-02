package com.bignerdranch.android.playlistmaker.search.ui.models

import androidx.compose.runtime.Immutable
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface SearchState {

    @Immutable
    object Loading : SearchState

    @Immutable
    data class Content(
        val tracks: ImmutableList<Track>
    ) : SearchState

    @Immutable
    data class History(
        val tracks: ImmutableList<Track>
    ) : SearchState

    @Immutable
    data class Error(
        val errorMessageId: Int
    ) : SearchState

    @Immutable
    data class Empty(
        val messageId: Int
    ) : SearchState
}