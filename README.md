# Test Types in This Suite

## 1. UI Smoke Tests
These tests quickly verify that important visible elements are present on the page and can be interacted with. They help confirm that the main UI is loading and that critical buttons, links, and menus are not broken.

## 2. Navigation Tests
These tests click links, buttons, and menu items to confirm that they navigate correctly. They check whether the action changes the page in the same tab or opens a new tab, then return to the original page so the rest of the suite can continue.

## 3. Regression Sanity Tests
These tests help catch issues after UI or code changes. They confirm that existing elements still exist, selectors still work, and previously working navigation paths have not been broken by updates.

## 4. Basic Functional Interaction Tests
These tests verify simple user interactions such as clicking menu options, opening popups, closing tabs, and returning to the original page. They confirm that the basic behavior of each control works as expected, even if they do not deeply validate the destination content.

Parallel: mvn test -Dsurefire.suiteXmlFiles=testng-parallel.xml
Sequential: mvn test -Dsurefire.suiteXmlFiles=testng.xml
