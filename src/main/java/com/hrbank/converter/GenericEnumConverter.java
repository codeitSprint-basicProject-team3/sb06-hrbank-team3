package com.hrbank.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

/*
 # GenericEnumConverter 주요 참고:
 1. Enum 클래스 정의: public abstract class Enum<E extends Enum<E>> implements Constable, Comparable<E>, Serializable
 2. converter 인터페이스 정의: public interface Converter<S, T>
 3. Enum.valueOf() : public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name)
 4. 제너릭 와일드카드:
  - <? extends T> : 와일드 카드의 상한 제한, T와 그 자손들만 가능
  - <? super T> : 와일드 카드의 하한 제한, T와 그 조상들만 가능
  - <?> : 제한 없음. 모든 타입 가능
 */
@RequiredArgsConstructor
public class GenericEnumConverter<T extends Enum<T>> implements Converter<String, T> {

  private final Class<T> enumType;

  @Override
  public T convert(String source) {
    try {
      return Enum.valueOf(enumType, source.toUpperCase());  // 대문자로 변환해서 Enum 값과 매핑
    } catch (Exception e) {
      throw new RuntimeException("지원하지 않는 값입니다: " + source);
    }
  }
}

