package com.nextcloud.client.onboarding

import android.accounts.Account
import android.content.res.Resources
import com.nextcloud.client.account.CurrentAccountProvider
import com.nextcloud.client.preferences.AppPreferences
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class OnboardingServiceTest {

    @Mock
    private lateinit var resources: Resources

    @Mock
    private lateinit var preferences: AppPreferences

    @Mock
    private lateinit var currentAccountProvider: CurrentAccountProvider

    @Mock
    private lateinit var account: Account

    private lateinit var onboardingService: OnboardingServiceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        onboardingService = OnboardingServiceImpl(resources, preferences, currentAccountProvider)
    }

    @Test
    fun `first run flag toggles with current current account`() {
        // GIVEN
        //      current account is not set
        //      first run flag is true
        assertTrue(onboardingService.isFirstRun)

        // WHEN
        //      current account is set
        whenever(currentAccountProvider.currentAccount).thenReturn(account)

        // THEN
        //      first run flag toggles
        assertFalse(onboardingService.isFirstRun)
    }
}
