import { test, expect } from '@playwright/test';

test('BBC Sport Football League Table - Liverpool won Premier League', async ({ page }) => {
  // Step 1: Navigate to BBC homepage
  await page.goto('https://www.bbc.co.uk/');

  // Step 2: Dismiss cookie banner if present
  const acceptCookies = page.getByRole('button', { name: /accept additional cookies/i });
  if (await acceptCookies.isVisible().catch(() => false)) {
    await acceptCookies.click();
  }

  // Step 3: Go to the Sport page (header link only)
  const sportLinks = await page.locator('header a:has-text("Sport")').all();
  if (sportLinks.length > 0) {
    await sportLinks[0].click();
  } else {
    throw new Error('Sport link not found in header');
  }
  await page.waitForLoadState('networkidle');

  // Step 4: Go to the Football page (header link only)
  const footballLinks = await page.locator('header a:has-text("Football")').all();
  if (footballLinks.length > 0) {
    await footballLinks[0].click();
  } else {
    throw new Error('Football link not found in header');
  }
  await page.waitForLoadState('networkidle');

  // Step 5: Go to the Tables/League Tables page
  const tablesLink = await page.locator('a:has-text("Tables")').first();
  await tablesLink.click();
  await page.waitForLoadState('networkidle');

  // Step 6: Confirm Liverpool won the Premier League
  // Look for a table row where Liverpool is in position 1
  const liverpoolRow = await page.locator('tr', { hasText: /liverpool/i }).first();
  // Get all cells in the row
  const cells = await liverpoolRow.locator('td').allTextContents();
  // The first cell should be the position, but sometimes it may be concatenated with the team name
  // Try to extract the number from the first cell
  const positionMatch = cells[0].match(/\d+/);
  expect(positionMatch?.[0]).toBe('1');
});
