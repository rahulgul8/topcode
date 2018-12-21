drop table if exists "user" cascade;
drop table if exists event_comment cascade;
drop table if exists event_comment_like cascade;
drop table if exists user_badge cascade;
drop table if exists user_event cascade;
drop table if exists user_reward_point cascade;
drop table if exists user_topic cascade;
drop table if exists notification cascade;
drop table if exists survey_answer cascade;
drop table if exists quiz_answer cascade;
drop table if exists user_reward cascade;

-- Mocked Tables - START ---

drop table if exists event_session cascade;
drop table if exists event_tags cascade;
drop table if exists event cascade;
drop table if exists topic;
drop table if exists carousel;
drop table if exists reward;
drop table if exists badge;
drop table if exists survey_answer_option;
drop table if exists survey_question;
drop table if exists quiz_answer_option;
drop table if exists quiz_question;

create table event (
    id uuid not null primary key,
    current_booking int4 not null,
    description varchar(255),
    landscape_image_url varchar(255),
    location varchar(255),
    max_capacity int4 not null,
    points_for_registering int4 not null,
    points_for_scanning_ticket int4 not null,
    points_for_submitting_survey int4 not null,
    points_for_submitting_quiz int4 not null,
    portrait_image_url varchar(255),
    title varchar(255),
    topic_id varchar(255),
    waiting_capacity int4 not null,
    first_session_start timestamp,
    last_session_end timestamp
);

create table event_tags (
    event_id uuid not null references event (id) on delete cascade,
    tags varchar(255)
);

create table event_session (
    id uuid not null primary key,
    "end" timestamp not null,
    location varchar(255),
    start timestamp not null,
    title varchar(255),
    speaker_name varchar(255),
    speaker_photo_url varchar(255),
    event_id uuid references event (id) on delete cascade
);

create table topic (
  id uuid not null primary key,
  icon_url varchar(255) not null,
  name varchar(255) not null
);

create table carousel (
  id uuid not null primary key,
  media_url varchar(255) not null,
  title varchar(255) not null
);

create table reward (
  id uuid not null primary key,
  icon_url varchar(255) not null,
  points_required int4 not null check (points_required>=1),
  expiration_in_hours int4 not null check (expiration_in_hours>=1),
  title varchar(255) not null
);

create table badge (
  id uuid not null primary key,
  icon_url varchar(255) not null,
  title varchar(255) not null
);

create table survey_question (
    id uuid not null primary key,
    question varchar(255) not null,
    type varchar(20) not null,
    event_id uuid not null references event (id) on delete cascade
);

create table survey_answer_option (
    id uuid not null primary key,
    answer_option varchar(255) not null,
    survey_question_id uuid not null references survey_question (id) on delete cascade
);

create table quiz_question (
    id uuid not null primary key,
    question varchar(255) not null,
    type varchar(20) not null,
    event_id uuid not null references event (id) on delete cascade
);

create table quiz_answer_option (
    id uuid not null primary key,
    answer_option varchar(255) not null,
    quiz_question_id uuid not null references quiz_question (id) on delete cascade
);

-- Mocked Tables - END   ---

create table "user" (
    id uuid not null primary key,
    division varchar(255),
    email varchar(255) not null unique,
    full_name varchar(255),
    location varchar(255),
    phone_number varchar(255) not null,
    position varchar(255),
    photo_url varchar(255),
    is_new boolean not null default true,
    notified_by_new_events boolean not null default true,
    points int4 not null default 0
);

create table event_comment (
    id uuid not null primary key,
    content varchar(1024),
    created_at timestamp not null,
    event_id uuid not null,
    like_count int not null default 0,
    deleted boolean not null default false,
    parent_comment_id uuid references event_comment (id) on delete cascade,
    user_id uuid not null references "user" (id) on delete cascade
);

create table event_comment_like (
    id uuid not null primary key,
    created_at timestamp not null,
    event_comment_id uuid not null references event_comment (id) on delete cascade,
    user_id uuid not null references "user" (id) on delete cascade
);

create table notification (
    id uuid not null primary key,
    created_at timestamp not null,
    content varchar(255) not null,
    icon_url varchar(255),
    read boolean not null,
    related_object_id uuid,
    related_object_type varchar(255),
    user_id uuid not null references "user" (id) on delete cascade
);

create table survey_answer (
    id uuid not null primary key,
    survey_question_id uuid not null,
    selected_survey_answer_option_id uuid,
    text_answer varchar(255),
    user_id uuid not null references "user" (id) on delete cascade
);

create table quiz_answer (
    id uuid not null primary key,
    quiz_question_id uuid not null,
    selected_quiz_answer_option_id uuid,
    text_answer varchar(255),
    user_id uuid not null references "user" (id) on delete cascade
);

create table user_badge (
    id uuid not null primary key,
    gained_at timestamp not null,
    badge_id uuid not null references badge (id) on delete cascade,
    user_id uuid not null references "user" (id) on delete cascade
);

create table user_event (
    id uuid not null primary key,
    event_id uuid not null,
    ticket_scanned boolean not null,
    submitted_survey boolean not null,
    submitted_quiz boolean not null,
    user_id uuid not null references "user" (id) on delete cascade
);

create table user_reward_point (
    id uuid not null primary key,
    created_at timestamp not null,
    description varchar(255),
    points int4 not null check (points>=1),
    user_id uuid not null references "user" (id) on delete cascade
);

create table user_topic (
    id uuid not null primary key,
    topic_id uuid not null references topic (id) on delete cascade,
    user_id uuid not null references "user" (id) on delete cascade
);

create table user_reward (
    id uuid not null primary key,
    expired_at timestamp not null,
    reward_id uuid not null references reward (id) on delete cascade,
    user_id uuid not null references "user" (id) on delete cascade
);
