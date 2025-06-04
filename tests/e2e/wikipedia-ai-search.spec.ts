import { test, expect } from '@playwright/test';

test('Wikipedia search for Artificial Intelligence', async ({ page }) => {
  // Step 1: Navigate to wikipedia.org
  await page.goto('https://wikipedia.org');

  // Step 2: Search for "Artificial Intelligence"
  await page.fill('input[name="search"]', 'Artificial Intelligence');
  await page.press('input[name="search"]', 'Enter');

  // Step 3: Wait for the main heading to appear
  await page.waitForSelector('#firstHeading');

  // Step 4: Verify the main heading contains 'Artificial intelligence'
  const heading = await page.textContent('#firstHeading');
  expect(heading?.toLowerCase()).toContain('artificial intelligence');
});
