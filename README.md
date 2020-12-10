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
#### main/controller/MemberController
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
