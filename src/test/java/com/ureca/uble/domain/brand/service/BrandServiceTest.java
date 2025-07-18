package com.ureca.uble.domain.brand.service;

import com.ureca.uble.domain.bookmark.repository.BookmarkRepository;
import com.ureca.uble.domain.brand.dto.response.BrandDetailRes;
import com.ureca.uble.domain.brand.dto.response.BrandListRes;
import com.ureca.uble.domain.brand.repository.BrandClickLogDocumentRepository;
import com.ureca.uble.domain.brand.repository.BrandRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Brand;
import com.ureca.uble.entity.Category;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.document.BrandClickLogDocument;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.Season;
import com.ureca.uble.domain.common.dto.response.CursorPageRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

	@Mock
	private BrandRepository brandRepository;

	@Mock
	private BookmarkRepository bookmarkRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BrandClickLogDocumentRepository brandClickLogDocumentRepository;

	@InjectMocks
	private BrandService brandService;

	@Test
	@DisplayName("브랜드 상세 정보를 조회한다.")
	void getBrandDetailSuccess() {
		//given
		Long userId = 1L;
		Long brandId = 2L;

		Category mockCategory = mock(Category.class);
		User mockUser = mock(User.class);
		when(mockCategory.getName()).thenReturn("푸드");
		when(mockUser.getRank()).thenReturn(Rank.VIP);
		when(mockUser.getGender()).thenReturn(Gender.FEMALE);

		Brand mockBrand = mock(Brand.class);
		when(mockBrand.getId()).thenReturn(brandId);
		when(mockBrand.getName()).thenReturn("투썸 플레이스");
		when(mockBrand.getCsrNumber()).thenReturn("1234-5678");
		when(mockBrand.getDescription()).thenReturn("설명");
		when(mockBrand.getImageUrl()).thenReturn("https://image.com");
		when(mockBrand.getSeason()).thenReturn(Season.SPRING);
		when(mockBrand.getCategory()).thenReturn(mockCategory);
		when(mockBrand.isVIPcock()).thenReturn(false);

		when(mockBrand.getBenefits()).thenReturn(List.of());
		when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));
		when(brandRepository.findWithBenefitsById(brandId)).thenReturn(Optional.of(mockBrand));
		when(bookmarkRepository.findByUserIdAndBrandId(userId, brandId)).thenReturn(Optional.empty());

		//when
		BrandDetailRes result = brandService.getBrandDetail(userId, brandId);

		//then
		assertThat(result.getBrandId()).isEqualTo(brandId);
		assertThat(result.isBookmarked()).isFalse();
		assertThat(result.getBookmarkId()).isNull();
		assertThat(result.getBenefits()).isEmpty();
	}

	@Test
	@DisplayName("브랜드 전체 목록을 커서 기반 페이지네이션을 이용하여 조회한다.")
	void getBrandListSuccess() {
		// given
		Long userId = 1L;
		Long lastBrandId = null;
		int size = 2;

		Category mockCategory = mock(Category.class);
		Rank mockMinRank = mock(Rank.class);
		when(mockMinRank.toString()).thenReturn("VIP");
		when(mockCategory.getName()).thenReturn("푸드");


		// 브랜드 1 mock
		Brand brand1 = mock(Brand.class);
		when(brand1.getId()).thenReturn(1L);
		when(brand1.getName()).thenReturn("브랜드1");
		when(brand1.getCategory()).thenReturn(mockCategory);
		when(brand1.getMinRank()).thenReturn(mockMinRank);
		when(brand1.getDescription()).thenReturn("브랜드1 설명");
		when(brand1.getImageUrl()).thenReturn("https://image1.com");

		// 브랜드 2 mock
		Brand brand2 = mock(Brand.class);
		when(brand2.getId()).thenReturn(2L);
		when(brand2.getName()).thenReturn("브랜드2");
		when(brand2.getCategory()).thenReturn(mockCategory);
		when(brand2.getMinRank()).thenReturn(mockMinRank);
		when(brand2.getDescription()).thenReturn("브랜드2 설명");
		when(brand2.getImageUrl()).thenReturn("https://image2.com");

		List<Brand> mockBrands = List.of(brand1, brand2);

		when(brandRepository.findWithFilterAndCursor(null, null, null, lastBrandId, size + 1))
			.thenReturn(mockBrands);
		when(bookmarkRepository.findByUserIdAndBrandId(eq(userId), anyLong()))
			.thenReturn(Optional.empty());

		// when
		CursorPageRes<BrandListRes> result = brandService.getBrandList(userId, null, null, null, lastBrandId, size);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.isHasNext()).isFalse();
		assertThat(result.getLastCursorId()).isEqualTo(2L);

		BrandListRes res1 = result.getContent().get(0);
		assertThat(res1.getBrandId()).isEqualTo(1L);
		assertThat(res1.getImgUrl()).isEqualTo("https://image1.com");
		assertThat(res1.isBookmarked()).isFalse();

		BrandListRes res2 = result.getContent().get(1);
		assertThat(res2.getBrandId()).isEqualTo(2L);
		assertThat(res2.getImgUrl()).isEqualTo("https://image2.com");
		assertThat(res2.isBookmarked()).isFalse();
	}
}
