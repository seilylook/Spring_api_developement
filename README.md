# Spring MVC 개발 - API 이용

## 2020 - 12 -09
### 회원 등록 API 구현
##### main/controller/MemberController

    @PostMapping("/api/v2/members")
        public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
    
            Member member = new Member();
            member.setName(request.getName());
    
            Long id = memberService.join(member);
            return new CreateMemberResponse(id);
    
        }

##### inner class 인 createMemberRequest 에서 사용자의 이름을 받아와 Member 객체의 member에 할당해준다.
##### 그리고 memberService 클래스에 접근하여 name 과 일치하는 회원 id 를 join 해준다. 
##### 마지막으로 회원 id를 return 해준다.

---------
## 2020 - 12 -10
### 회원 정보 수정 기능 구현
#### main/java/jpabook.jpashop/api/MemberController
    @PutMapping("/api/v2/members/{id}")
        public UpdateMemberResponse updateMemberResponse (
                @PathVariable("id") Long id,
                @RequestBody @Valid UpdateMemberRequest request) {
    
            memberService.update(id, request.getName());
            Member findMember = memberService.findOne(id);
            return new UpdateMemberResponse(findMember.getId(), findMember.getName());
        }
    
        @Data
        static class UpdateMemberRequest {
    
            private String name;
    
        }
    
        @Data
        @AllArgsConstructor
        static class UpdateMemberResponse {
    
            private Long id;
            private String name;
    
        }
##### memberService 클래스에 update 메소드를 만들어주고 id, request를 통해 얻은 회원 name을 넘겨준다.
##### 그리고 memberService 에서 넘겨준 id와 일치하는 회원 정보를 findMember 에 저장해준다. 
##### 마지막으로 찾은 회원의 이름, id (findMember.getId(), findMember.getName()) 을 return 해준다.

---------------
## 20202 - 12 - 11
### 회원 조회 기능 구현 
#### main/java/jpabook.jpashop/api/MemberController

    @GetMapping("/api/v2/members")
        public Result membersV2() {
            List<Member> findMembers = memberService.findMembers();
            List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName()))
                    .collect(Collectors.toList());
            return new Result(collect);
        }
    
        @Data
        @AllArgsConstructor
        static class Result<T> {
            private T data;
        }
    
        @Data
        @AllArgsConstructor
        static class MemberDto {
            private String name;
    
        }
    
    
##### entity 직접 반환하거나 노출하는 로직은 절대로 만들어서 안된다!
##### 예를 들어, Member entity에 직접 접근해서 @JsonIgnore 같은 annotation을 넣어주는 행위
##### 만약에 이런 행위를 할 시 양방향 연결이 이루어져 error detection 및 수정이 불가능해진다. 
##### @GetMapping("/api/v2/members") 로 끌어와서 memberservice의 findMembers를 통해 회원을 찾는다.
##### 그리고 회원 목록이 array로 넘어오기에 이를 stream.map에 ramda 를 이용해 memberDto class에 넘겨준다.
###### DTO(Data Transfer Object)는 VO(Value Object)로 바꿔 말할 수 있는데 계층간 데이터 교환을 위한 자바빈즈를 말합니다.
##### MemberDto 클래스에서 만들어진 회원 이름들을 collect(Collectors.toList()) 로 list로 만들어준다.
##### 마지막으로 결과를 담는 generic class Result<T> 객체를 선언하고 값을 보내준다.

