package com.hrbank.employee;

import com.hrbank.employee.dto.EmployeeSearchCondition;
import com.hrbank.employee.dto.EmployeeSearchResult;
import com.hrbank.employee.enums.SortDirection;
import com.hrbank.employee.enums.SortField;
import com.hrbank.file.QFile;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QEmployee employee = QEmployee.employee;
    private final QFile file =  QFile.file;
    private final QDepartment department = QDepartment.department;


    // 최종 조회 목록 반납 ( 정렬된 직원 리스트 + 총 직원 수 + 다음 페이지 존재 여부)
    @Override
    public EmployeeSearchResult searchEmployees(EmployeeSearchCondition searchCondition) {

        // 정렬된 직원 리스트 및 조건
        EmployeeQueryResult queryResult = searchFilteredEmployees(searchCondition);
        List<Employee> employees = queryResult.employees;
        Long totalElements = searchFilteredEmployeeCount(queryResult.builder);

        Boolean hasNext = false;

        if (employees.size() > searchCondition.size()) {
            hasNext = true;
            employees = employees.subList(0, searchCondition.size());
        }

        return new EmployeeSearchResult(employees, totalElements, hasNext);
    }

    // 직원 조회 목록 ( 정렬/페이지네이션 된 직원 리스트 + 조회 시 사용된 최종 조건-페이지네이션 제외- 반납 )
    private EmployeeQueryResult searchFilteredEmployees(EmployeeSearchCondition searchCondition) {
        BooleanBuilder builder =  new BooleanBuilder();

        // 조건: 이름 or 이메일 (부분 일치)
        if (searchCondition.nameOrEmail() != null) {
            BooleanBuilder nameOrEmail = new BooleanBuilder();
            nameOrEmail.and(employee.name.contains(searchCondition.nameOrEmail()));
            nameOrEmail.or(employee.email.contains(searchCondition.nameOrEmail()));
            builder.and(nameOrEmail);
        }

        // 조건: 부서명 (부분 일치)
        if (searchCondition.departmentName() != null) {
            builder = builder.and(employee.department.name.contains(searchCondition.departmentName()));
        }

        // 조건: 직함 (부분 일치)
        if (searchCondition.position() != null) {
            builder = builder.and(employee.position.contains(searchCondition.position()));
        }

        // 조건: 사원 번호 (부분 일치)
        if (searchCondition.employeeNumber() != null) {
            builder = builder.and(employee.employeeNumber.contains(searchCondition.employeeNumber()));
        }

        // 조건: 입사일 (범위 일치)
        if (searchCondition.hireDateFrom() != null && searchCondition.hireDateTo() != null) {
            builder = builder.and(employee.hireDate.between(searchCondition.hireDateFrom(), searchCondition.hireDateTo()));
        }

        // 조건: 상태 (완전 일치)
        if (searchCondition.status() != null) {
            builder = builder.and(employee.status.eq(searchCondition.status()));
        }

        // 페이지네이션 조건 미포함 상태의 최종 조건
        BooleanBuilder builderWithoutPagination = new BooleanBuilder(builder.getValue());

        // 정렬 기준
        OrderSpecifier orderBy = null;
        switch (searchCondition.sortField()) {
            case name: {
                if (searchCondition.sortDirection() == SortDirection.asc)
                    orderBy = employee.name.asc();
                else
                    orderBy = employee.name.desc();
            } break;
            case hireDate: {
                if (searchCondition.sortDirection() == SortDirection.asc)
                    orderBy = employee.hireDate.asc();
                else
                    orderBy = employee.hireDate.desc();
            } break;
            case employeeNumber: {
                if (searchCondition.sortDirection() == SortDirection.asc)
                    orderBy = employee.employeeNumber.asc();
                else
                    orderBy = employee.employeeNumber.desc();
            } break;
        }

        // 첫 페이지가 아닐 시
        if (searchCondition.idAfter() != null) {
            cursorPagination(builder, searchCondition);
        }

        List<Employee> employees = queryFactory
                .selectFrom(employee)
                .innerJoin(employee.department, department).fetchJoin()
                .leftJoin(employee.file, file).fetchJoin()
                .where(builder)
                .orderBy(orderBy)
                .limit(searchCondition.size() + 1) // hasNext를 위해 그 다음 요소까지 조회
                .fetch();


        return new EmployeeQueryResult(employees, builderWithoutPagination);
    }

    // 다수 필드 반환 받기 위한 레포지토리 내 DTO
    private record EmployeeQueryResult (
            List<Employee> employees,
            BooleanBuilder builder
    ){}

    // 조건 파라미터를 만족하는 직원 수 count
    private Long searchFilteredEmployeeCount(BooleanBuilder builder) {
        return Optional.ofNullable(
                queryFactory
                .select(employee.count())
                .where(builder)
                .fetchOne())
                .orElse(0L);
    }

    // 페이지네이션 메소드
    private void cursorPagination(BooleanBuilder builder, EmployeeSearchCondition searchCondition) {
        BooleanBuilder cursorCondition = new BooleanBuilder();

        // 기준: 이름
        if (searchCondition.sortField() == SortField.name) {
            BooleanBuilder cursorNameCondition = new BooleanBuilder();
            if (searchCondition.sortDirection() == SortDirection.asc) { // 오름차순
                // (A) 이름
                cursorCondition.and(employee.name.gt(searchCondition.idAfterName()));

                // (B AND C) 이름 = 마지막 이름 AND ID > 마지막 ID
                cursorNameCondition.and(employee.name.eq(searchCondition.idAfterName()));
                cursorNameCondition.and(employee.id.gt(searchCondition.idAfter()));
            }
            else { // 내림차순
                cursorCondition.and(employee.name.lt(searchCondition.idAfterName()));

                cursorNameCondition.and(employee.name.eq(searchCondition.idAfterName()));
                cursorNameCondition.and(employee.id.lt(searchCondition.idAfter()));
            }
            // A OR (B AND C)
            cursorCondition.or(cursorNameCondition);
        }

        // 기준: 사원 번호
        if (searchCondition.sortField() == SortField.employeeNumber) {
            BooleanBuilder cursorEmployeeNumberCondition = new BooleanBuilder();
            if (searchCondition.sortDirection() == SortDirection.asc) {
                cursorCondition.and(employee.employeeNumber.gt(searchCondition.idAfterEmployeeNumber()));

                cursorEmployeeNumberCondition.and(employee.employeeNumber.eq(searchCondition.idAfterEmployeeNumber()));
                cursorEmployeeNumberCondition.and(employee.id.gt(searchCondition.idAfter()));
            }
            else {
                cursorCondition.and(employee.employeeNumber.lt(searchCondition.idAfterEmployeeNumber()));

                cursorEmployeeNumberCondition.and(employee.employeeNumber.eq(searchCondition.idAfterEmployeeNumber()));
                cursorEmployeeNumberCondition.and(employee.id.lt(searchCondition.idAfter()));
            }
            cursorCondition.or(cursorEmployeeNumberCondition);
        }

        // 기준: 입사일
        if (searchCondition.sortField() == SortField.hireDate) {
            BooleanBuilder cursorHireDateCondition = new BooleanBuilder();
            if (searchCondition.sortDirection() == SortDirection.asc) {
                cursorCondition.and(employee.hireDate.gt(searchCondition.idAfterHireDate()));

                cursorHireDateCondition.and(employee.hireDate.eq(searchCondition.idAfterHireDate()));
                cursorHireDateCondition.and(employee.id.gt(searchCondition.idAfter()));}
            else {
                cursorCondition.and(employee.hireDate.lt(searchCondition.idAfterHireDate()));

                cursorHireDateCondition.and(employee.hireDate.eq(searchCondition.idAfterHireDate()));
                cursorHireDateCondition.and(employee.id.lt(searchCondition.idAfter()));}
            cursorCondition.or(cursorHireDateCondition);
        }

        builder.and(cursorCondition);
    }
}
