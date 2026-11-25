import time
import os
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By

os.makedirs(r"c:\Users\hp\Downloads\EPS\docs\screenshots", exist_ok=True)
output_dir = r"c:\Users\hp\Downloads\EPS\docs\screenshots"

options = Options()
options.add_argument('--headless=new')
options.add_argument('--window-size=1440,900')
options.binary_location = r"C:\Program Files\Google\Chrome\Application\chrome.exe"

driver = webdriver.Chrome(options=options)

try:
    # 1. Login Page Screenshot
    print("Capturing Login page...")
    driver.get("http://localhost:8080/eps/login")
    time.sleep(1)
    driver.save_screenshot(os.path.join(output_dir, "screenshot_login.png"))

    # 2. Admin Dashboard Screenshot
    print("Capturing Admin Dashboard...")
    driver.get("http://localhost:8080/eps/login")
    driver.find_element(By.ID, "username").clear()
    driver.find_element(By.ID, "username").send_keys("admin")
    driver.find_element(By.ID, "password").clear()
    driver.find_element(By.ID, "password").send_keys("Admin@123")
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
    time.sleep(2)
    driver.save_screenshot(os.path.join(output_dir, "screenshot_admin.png"))

    # Admin Reports Screenshot
    print("Capturing Admin Reports...")
    driver.get("http://localhost:8080/eps/admin/reports")
    time.sleep(2)
    driver.save_screenshot(os.path.join(output_dir, "screenshot_reports.png"))

    # 3. Manager Dashboard Screenshot
    print("Capturing Manager Dashboard...")
    driver.get("http://localhost:8080/eps/logout")
    time.sleep(1)
    driver.get("http://localhost:8080/eps/login")
    driver.find_element(By.ID, "username").clear()
    driver.find_element(By.ID, "username").send_keys("manager1")
    driver.find_element(By.ID, "password").clear()
    driver.find_element(By.ID, "password").send_keys("Manager@123")
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
    time.sleep(2)
    driver.save_screenshot(os.path.join(output_dir, "screenshot_manager.png"))

    # Manager Evaluate Form Screenshot
    print("Capturing Manager Evaluate Form...")
    driver.get("http://localhost:8080/eps/manager/evaluations?action=evaluate&employeeId=1")
    time.sleep(1)
    driver.save_screenshot(os.path.join(output_dir, "screenshot_evaluate.png"))

    # 4. Employee Dashboard Screenshot
    print("Capturing Employee Dashboard...")
    driver.get("http://localhost:8080/eps/logout")
    time.sleep(1)
    driver.get("http://localhost:8080/eps/login")
    driver.find_element(By.ID, "username").clear()
    driver.find_element(By.ID, "username").send_keys("emp1")
    driver.find_element(By.ID, "password").clear()
    driver.find_element(By.ID, "password").send_keys("Employee@123")
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
    time.sleep(2)
    driver.save_screenshot(os.path.join(output_dir, "screenshot_employee.png"))

    print("ALL SCREENSHOTS CAPTURED SUCCESSFULLY!")

finally:
    driver.quit()
