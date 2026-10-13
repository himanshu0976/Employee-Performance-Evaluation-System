-- ========================================================================
-- Employee Performance Evaluation System (EPS)
-- Sample Seed Data
-- ========================================================================

USE eps_db;

-- ------------------------------------------------------------------------
-- 1. Seed Departments (3 Departments)
-- ------------------------------------------------------------------------
INSERT INTO departments (id, name, code, description) VALUES
(1, 'Engineering', 'ENG', 'Software engineering, system architecture, QA, and cloud infrastructure'),
(2, 'Marketing', 'MKT', 'Product marketing, digital growth campaigns, branding, and communications'),
(3, 'Human Resources', 'HR', 'Talent acquisition, employee development, benefits, and workplace culture')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ------------------------------------------------------------------------
-- 2. Seed Users (1 Admin, 2 Managers, 10 Employees)
-- Passwords:
-- Admin:    Admin@123    ($2a$10$bCY6.H/tNX3pu.n1EQdyz.KYmhNfLz2TfpXWGvZZujQrI14hecPIi)
-- Manager:  Manager@123  ($2a$10$a7a0i2buhFhiKWcMREJvaOf96GcYgGkRVg.iYI2X2PNgc6H9rbUfO)
-- Employee: Employee@123 ($2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS)
-- ------------------------------------------------------------------------
INSERT INTO users (id, username, password_hash, role, full_name, email, phone, is_active) VALUES
-- Admin
(1, 'admin', '$2a$10$bCY6.H/tNX3pu.n1EQdyz.KYmhNfLz2TfpXWGvZZujQrI14hecPIi', 'ADMIN', 'Eleanor Vance (Admin)', 'admin@eps.com', '+1-555-0100', 1),

-- Managers (User IDs 2, 3)
(2, 'manager1', '$2a$10$a7a0i2buhFhiKWcMREJvaOf96GcYgGkRVg.iYI2X2PNgc6H9rbUfO', 'MANAGER', 'Sarah Connor', 'sarah.connor@eps.com', '+1-555-0101', 1),
(3, 'manager2', '$2a$10$a7a0i2buhFhiKWcMREJvaOf96GcYgGkRVg.iYI2X2PNgc6H9rbUfO', 'MANAGER', 'David Miller', 'david.miller@eps.com', '+1-555-0102', 1),

-- Employees (User IDs 4 through 13)
(4,  'emp1',  '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'Alice Johnson',   'alice.johnson@eps.com',   '+1-555-0201', 1),
(5,  'emp2',  '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'Bob Smith',       'bob.smith@eps.com',       '+1-555-0202', 1),
(6,  'emp3',  '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'Charlie Brown',   'charlie.brown@eps.com',   '+1-555-0203', 1),
(7,  'emp4',  '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'Diana Prince',    'diana.prince@eps.com',    '+1-555-0204', 1),
(8,  'emp5',  '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'Ethan Hunt',      'ethan.hunt@eps.com',      '+1-555-0205', 1),
(9,  'emp6',  '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'Fiona Gallagher', 'fiona.gallagher@eps.com', '+1-555-0206', 1),
(10, 'emp7',  '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'George Clark',    'george.clark@eps.com',    '+1-555-0207', 1),
(11, 'emp8',  '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'Hannah Abbott',   'hannah.abbott@eps.com',   '+1-555-0208', 1),
(12, 'emp9',  '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'Ian Wright',      'ian.wright@eps.com',      '+1-555-0209', 1),
(13, 'emp10', '$2a$10$4xfTcR4H69bqk7y7.2tK7O2vTX.pRm6uULQn2qA5QheG1yFbBpfNS', 'EMPLOYEE', 'Julia Roberts',   'julia.roberts@eps.com',   '+1-555-0210', 1)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- ------------------------------------------------------------------------
-- 3. Seed Employees Table (10 Employee Profiles with Department and Manager)
-- Manager 1 (Sarah Connor, User ID 2) manages Engineering (EMP 1 to 5)
-- Manager 2 (David Miller, User ID 3) manages Marketing & HR (EMP 6 to 10)
-- ------------------------------------------------------------------------
INSERT INTO employees (id, user_id, employee_code, department_id, manager_id, designation, hire_date, salary) VALUES
(1,  4,  'EMP-1001', 1, 2, 'Senior Software Engineer',    '2022-03-15', 95000.00),
(2,  5,  'EMP-1002', 1, 2, 'QA Lead Engineer',            '2022-06-01', 82000.00),
(3,  6,  'EMP-1003', 1, 2, 'DevOps Engineer',             '2023-01-10', 88000.00),
(4,  7,  'EMP-1004', 1, 2, 'Frontend Developer',          '2023-04-20', 75000.00),
(5,  8,  'EMP-1005', 1, 2, 'Junior Backend Developer',    '2024-02-01', 65000.00),
(6,  9,  'EMP-2001', 2, 3, 'Senior Marketing Strategist', '2021-11-15', 85000.00),
(7,  10, 'EMP-2002', 2, 3, 'Content & Brand Lead',        '2022-08-01', 72000.00),
(8,  11, 'EMP-2003', 2, 3, 'Growth & SEO Specialist',     '2023-05-12', 68000.00),
(9,  12, 'EMP-3001', 3, 3, 'HR Business Partner',         '2022-01-15', 78000.00),
(10, 13, 'EMP-3002', 3, 3, 'Talent Acquisition Lead',     '2023-09-01', 71000.00)
ON DUPLICATE KEY UPDATE employee_code=VALUES(employee_code);

-- ------------------------------------------------------------------------
-- 4. Seed Evaluation Criteria (6 Criteria summing to 100% weight)
-- ------------------------------------------------------------------------
INSERT INTO evaluation_criteria (id, name, description, weightage, is_active) VALUES
(1, 'Attendance',       'Punctuality, regularity, schedule adherence, and attendance record',                15.00, 1),
(2, 'Work Quality',      'Accuracy, attention to detail, standard compliance, and excellence of deliverables', 20.00, 1),
(3, 'Productivity',      'Efficiency, volume of completed work, and meeting deadline commitments',            20.00, 1),
(4, 'Teamwork',          'Collaboration, willingness to assist peers, and cross-functional synergy',         15.00, 1),
(5, 'Communication',     'Clarity, transparency, active listening, and constructive reporting',               15.00, 1),
(6, 'Goal Achievement',  'Attainment of designated quarterly and annual milestones and KPIs',                 15.00, 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ------------------------------------------------------------------------
-- 5. Seed Evaluation Cycles (2 Cycles: 1 Completed, 1 Active)
-- ------------------------------------------------------------------------
INSERT INTO evaluation_cycles (id, title, description, start_date, end_date, status) VALUES
(1, 'Annual Performance Review 2025', 'Comprehensive annual performance review for FY2025', '2025-01-01', '2025-12-31', 'COMPLETED'),
(2, 'Mid-Year Review 2026',           'Mid-year performance assessment and OKR progress review for 2026', '2026-06-01', '2026-12-31', 'ACTIVE')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- ------------------------------------------------------------------------
-- 6. Seed Evaluations (Completed Cycle 1 + Active Cycle 2 mixed)
-- ------------------------------------------------------------------------
-- Cycle 1 (Completed for all 10 employees)
INSERT INTO evaluations (id, cycle_id, employee_id, evaluator_id, total_score, rating, status, summary_feedback, strengths, areas_for_improvement, evaluated_at) VALUES
(1,  1, 1,  2, 4.80, 'Outstanding',          'APPROVED', 'Alice showed exceptional leadership and technical architecture prowess this year.', 'Deep cloud expertise, mentorship, flawless system design', 'Continue expanding cross-team presentations', '2025-12-15 14:30:00'),
(2,  1, 2,  2, 4.15, 'Exceeds Expectations', 'APPROVED', 'Bob consistently delivered thorough test automation coverage for critical releases.', 'Proactive bug identification, strong teamwork', 'Further automate end-to-end regression suites', '2025-12-16 11:00:00'),
(3,  1, 3,  2, 3.20, 'Meets Expectations',   'APPROVED', 'Charlie maintained steady CI/CD pipeline uptime with satisfactory turnaround.', 'Quick incident response, dependable attendance', 'Improve deployment documentation and communication', '2025-12-17 16:15:00'),
(4,  1, 4,  2, 3.90, 'Exceeds Expectations', 'APPROVED', 'Diana revamped key UI workflows with high fidelity and design consistency.', 'Design empathy, prompt turnaround, clean code', 'Strengthen unit testing coverage in front-end apps', '2025-12-18 10:00:00'),
(5,  1, 5,  2, 2.30, 'Needs Improvement',    'APPROVED', 'Ethan made good progress on basic modules but struggled with complex deadlines.', 'Curiosity to learn, good attitude', 'Requires closer supervision on task estimates and code quality', '2025-12-19 15:45:00'),
(6,  1, 6,  3, 4.65, 'Outstanding',          'APPROVED', 'Fiona spearheaded the Q3 product launch which surpassed lead generation targets by 40%.', 'High initiative, data-driven strategy, stellar results', 'Scale delegation to junior marketing associates', '2025-12-15 09:30:00'),
(7,  1, 7,  3, 3.80, 'Exceeds Expectations', 'APPROVED', 'George produced engaging whitepapers and blogs that elevated company brand presence.', 'High quality writing, excellent storytelling', 'Ensure calendar adherence during peak campaign cycles', '2025-12-16 14:00:00'),
(8,  1, 8,  3, 3.10, 'Meets Expectations',   'APPROVED', 'Hannah sustained organic search keyword rankings across the main domains.', 'Analytical mindset, consistent reporting', 'Explore innovative technical SEO experiments', '2025-12-17 11:30:00'),
(9,  1, 9,  3, 2.95, 'Meets Expectations',   'APPROVED', 'Ian resolved employee inquiries reliably and organized company onboarding sessions.', 'Approachable demeanor, good organizational skills', 'Focus on strategic HR metric tracking', '2025-12-18 13:00:00'),
(10, 1, 10, 3, 1.40, 'Unsatisfactory',       'APPROVED', 'Julia missed multiple hiring quotas and lacked required follow-through with candidates.', 'Friendly interpersonal presence', 'Urgent improvement needed in candidate pipeline management and turnaround', '2025-12-19 17:00:00'),

-- Cycle 2 (Active Cycle: 3 SUBMITTED, 2 DRAFT, remaining 5 PENDING)
(11, 2, 1, 2, 4.85, 'Outstanding',          'SUBMITTED', 'Alice continues her outstanding pace with our microservice migration in H1.', 'Exceptional problem solving, peer support', 'Keep grooming the next tech lead', '2026-07-10 10:00:00'),
(12, 2, 2, 2, 4.25, 'Exceeds Expectations', 'SUBMITTED', 'Bob has done great work introducing Cypress automation framework.', 'High diligence, solid test coverage', 'Encourage team wide test writing standards', '2026-07-11 11:30:00'),
(13, 2, 6, 3, 4.70, 'Outstanding',          'SUBMITTED', 'Fiona delivered outstanding customer acquisition cost reduction metrics.', 'Strategic mindset, high energy, cross-functional execution', 'Explore international market campaigns', '2026-07-12 15:00:00'),
(14, 2, 3, 2, 3.30, 'Meets Expectations',   'DRAFT',     'Initial notes for Charlie: Good progress on Kubernetes cluster upgrade, pending final review.', 'Dependable, stable operations', 'Update runbooks', NULL),
(15, 2, 7, 3, 3.85, 'Exceeds Expectations', 'DRAFT',     'Draft review for George: Excellent social campaign copywriting, awaiting H1 metrics.', 'Creative flair, strong communication', 'Timeliness on impromptu requests', NULL)
ON DUPLICATE KEY UPDATE total_score=VALUES(total_score);

-- ------------------------------------------------------------------------
-- 7. Seed Evaluation Scores (Criteria 1-6 for all evaluations)
-- ------------------------------------------------------------------------
-- Evaluation 1 (Emp 1, Cycle 1 - 4.80)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(1, 1, 5.0, 'Never late, 100% attendance throughout the fiscal year.'),
(1, 2, 5.0, 'Zero defects reported in production modules architected by Alice.'),
(1, 3, 4.5, 'Consistently delivers complex stories ahead of sprint deadlines.'),
(1, 4, 5.0, 'Beloved by teammates for helpful code reviews and mentoring.'),
(1, 5, 4.5, 'Clear documentation and concise architecture diagrams.'),
(1, 6, 5.0, 'Exceeded all key project delivery milestones.');

-- Evaluation 2 (Emp 2, Cycle 1 - 4.15)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(2, 1, 4.5, 'Regular and punctual attendance.'),
(2, 2, 4.5, 'Excellent quality test plans and bug reports.'),
(2, 3, 4.0, 'Maintains a steady pace of test case creation.'),
(2, 4, 4.0, 'Collaborates smoothly with developers to reproduce tricky issues.'),
(2, 5, 4.0, 'Provides detailed notes in Jira tickets.'),
(2, 6, 4.0, 'Hit QA target milestones on all quarterly releases.');

-- Evaluation 3 (Emp 3, Cycle 1 - 3.20)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(3, 1, 3.5, 'Occasional unexpected absences, but communicates reasonably.'),
(3, 2, 3.0, 'Script configurations sometimes require minor rework.'),
(3, 3, 3.5, 'Handles server provisioning with standard turnaround.'),
(3, 4, 3.0, 'Works fine when assigned tasks; should reach out more proactively.'),
(3, 5, 3.0, 'Could improve status updates during infrastructure outages.'),
(3, 6, 3.2, 'Met core uptime goals for the primary cluster.');

-- Evaluation 4 (Emp 4, Cycle 1 - 3.90)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(4, 1, 4.0, 'Good attendance record and timely arrivals.'),
(4, 2, 4.0, 'Delivers clean and responsive UI components.'),
(4, 3, 4.0, 'Keeps up with agile sprints without spilling tasks.'),
(4, 4, 4.0, 'Works closely with UX designers to ensure brand fidelity.'),
(4, 5, 3.5, 'Good communicator, occasionally silent in standups.'),
(4, 6, 4.0, 'Successfully shipped the redesigned customer portal.');

-- Evaluation 5 (Emp 5, Cycle 1 - 2.30)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(5, 1, 3.0, 'Acceptable attendance but takes irregular breaks.'),
(5, 2, 2.0, 'Frequent bugs found during code reviews; needs to test locally.'),
(5, 3, 2.0, 'Slower completion rate than team average for junior dev.'),
(5, 4, 2.5, 'Hesitant to ask seniors for assistance when blocked.'),
(5, 5, 2.5, 'Needs to communicate roadblocks earlier in sprints.'),
(5, 6, 2.0, 'Missed multiple feature sprint delivery dates.');

-- Evaluation 6 (Emp 6, Cycle 1 - 4.65)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(6, 1, 5.0, 'Flawless attendance record.'),
(6, 2, 4.5, 'Campaign collateral and landing page copy were top tier.'),
(6, 3, 5.0, 'Extremely productive output across multiple advertising channels.'),
(6, 4, 4.5, 'Energizes the marketing team and partners well with Sales.'),
(6, 5, 4.5, 'Crystal clear presentations to company leadership.'),
(6, 6, 4.5, 'Exceeded annual MQL (Marketing Qualified Leads) target by 40%.');

-- Evaluation 7 (Emp 7, Cycle 1 - 3.80)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(7, 1, 4.0, 'Solid attendance and dependability.'),
(7, 2, 4.0, 'High quality written content and technical articles.'),
(7, 3, 3.5, 'Sometimes struggles with overlapping campaign deadlines.'),
(7, 4, 4.0, 'Cooperative team member, receptive to editor feedback.'),
(7, 5, 4.0, 'Expressive writer with strong audience resonance.'),
(7, 6, 3.5, 'Published 24 articles out of 25 planned targets.');

-- Evaluation 8 (Emp 8, Cycle 1 - 3.10)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(8, 1, 3.5, 'Good attendance record.'),
(8, 2, 3.0, 'SEO keyword audit reports are adequate.'),
(8, 3, 3.0, 'Output meets baseline targets but could be expedited.'),
(8, 4, 3.0, 'Collaborates well with copywriters when prompted.'),
(8, 5, 3.0, 'Presents data clearly in weekly reporting calls.'),
(8, 6, 3.2, 'Achieved modest organic search ranking uplift.');

-- Evaluation 9 (Emp 9, Cycle 1 - 2.95)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(9, 1, 3.5, 'Reliable attendance and prompt in office hours.'),
(9, 2, 3.0, 'Employee records maintained accurately.'),
(9, 3, 2.5, 'Grievance and inquiry resolution times need improvement.'),
(9, 4, 3.0, 'Coordinates well with department managers.'),
(9, 5, 3.0, 'Empathetic listener, good interpersonal touch.'),
(9, 6, 2.8, 'Completed core onboarding training sessions.');

-- Evaluation 10 (Emp 10, Cycle 1 - 1.40)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(10, 1, 2.0, 'Unscheduled leaves caused delays in critical hiring rounds.'),
(10, 2, 1.0, 'Multiple resume evaluation errors and mismatched candidates.'),
(10, 3, 1.0, 'Time-to-hire metric doubled compared to target SLA.'),
(10, 4, 2.0, 'Disconnected from hiring managers requirements.'),
(10, 5, 1.5, 'Poor communication and delayed responses to applicants.'),
(10, 6, 1.0, 'Failed to close open requisitions for 2 quarters.');

-- Evaluation 11 (Emp 1, Cycle 2 - 4.85 - Active Cycle Submitted)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(11, 1, 5.0, 'Perfect attendance in H1 2026.'),
(11, 2, 5.0, 'Architected new event driven queue with zero errors.'),
(11, 3, 5.0, 'Top contributor in git commits and velocity.'),
(11, 4, 4.5, 'Mentors new joiners proactively.'),
(11, 5, 4.5, 'Engaging sprint review demos.'),
(11, 6, 5.0, 'All Q1 and Q2 milestones delivered on target.');

-- Evaluation 12 (Emp 2, Cycle 2 - 4.25 - Active Cycle Submitted)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(12, 1, 4.5, 'Always on time for daily triage.'),
(12, 2, 4.5, 'Comprehensive test suites prevented two major regressions.'),
(12, 3, 4.0, 'Consistent execution speed across test suites.'),
(12, 4, 4.5, 'Excellent collaboration with backend engineers.'),
(12, 5, 4.0, 'Concise test metric reports.'),
(12, 6, 4.0, 'Achieved 85% automated test coverage goal.');

-- Evaluation 13 (Emp 6, Cycle 2 - 4.70 - Active Cycle Submitted)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(13, 1, 5.0, 'Exemplary punctuality and professional presence.'),
(13, 2, 4.5, 'High converting landing pages and creative assets.'),
(13, 3, 5.0, 'Remarkable project turnaround in rapid A/B tests.'),
(13, 4, 4.5, 'Works seamlessly with design and product teams.'),
(13, 5, 4.5, 'Compelling executive stakeholder summaries.'),
(13, 6, 4.8, 'Smashed mid-year lead generation goal by 25%.');

-- Evaluation 14 (Emp 3, Cycle 2 - 3.30 - Draft)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(14, 1, 4.0, 'Good attendance so far in 2026.'),
(14, 2, 3.0, 'Configuration reviews in progress.'),
(14, 3, 3.5, 'Kubernetes rollout on schedule.'),
(14, 4, 3.0, 'Standard coordination.'),
(14, 5, 3.0, 'Drafting communications for upcoming outage.'),
(14, 6, 3.5, 'Cluster migration 70% complete.');

-- Evaluation 15 (Emp 7, Cycle 2 - 3.85 - Draft)
INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) VALUES
(15, 1, 4.0, 'Prompt and dependable.'),
(15, 2, 4.0, 'High quality content calendar draft.'),
(15, 3, 4.0, 'Completed newsletter pipeline.'),
(15, 4, 4.0, 'Active team contributor.'),
(15, 5, 4.0, 'Very receptive to creative feedback.'),
(15, 6, 3.5, 'On track for mid-year publishing targets.');
