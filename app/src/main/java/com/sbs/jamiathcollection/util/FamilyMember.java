package com.sbs.jamiathcollection.util;

public class FamilyMember {
    private String memberName;
    private String memberId;
    public FamilyMember() {
    }

    public FamilyMember(String memberId,String memberName) {
        this.memberId = memberId;
        this.memberName=memberName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
