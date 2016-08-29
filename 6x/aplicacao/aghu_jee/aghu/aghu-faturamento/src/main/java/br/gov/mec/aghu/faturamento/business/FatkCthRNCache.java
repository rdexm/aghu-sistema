package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemProcedimento;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerItemSusVO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.CacheMap;


@RequestScoped
public class FatkCthRNCache implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2676993765515301844L;

	private static final Log LOG = LogFactory.getLog(FatkCthRNCache.class);

	@EJB
	private FatkCthRN fatkCthRN;
	
	private Map<RnCthcVerItemSusInner, RnCthcVerItemSusVO> vos = new CacheMap<RnCthcVerItemSusInner, RnCthcVerItemSusVO>(5000);

	public RnCthcVerItemSusVO rnCthcVerItemSus(DominioOrigemProcedimento pModulo, Short pCnvCodigo, Byte pCspSeq, Short pQuantidade,
			Integer pPhiSeq) throws BaseException {
		RnCthcVerItemSusInner innerObject = new RnCthcVerItemSusInner(pCnvCodigo, pCspSeq, pQuantidade, pPhiSeq);

		RnCthcVerItemSusVO vo = this.vos.get(innerObject);

		if (vo == null) {
			vo = this.getFatkCthRN().rnCthcVerItemSus(pModulo, pCnvCodigo, pCspSeq, pQuantidade, pPhiSeq, null);
			this.vos.put(innerObject, vo);
		} else {
			if (Boolean.TRUE.equals(vo.getRetorno())) {
				LOG.debug("--- >>> retorno verdadeiro " + vo.getCodSus());
			}
		}
		
		return vo;
	}

	private class RnCthcVerItemSusInner {

		private Short pCnvCodigo;

		private Byte pCspSeq;

		private Short pQuantidade;

		private Integer pPhiSeq;

		public RnCthcVerItemSusInner(Short pCnvCodigo, Byte pCspSeq, Short pQuantidade, Integer pPhiSeq) {
			this.pCnvCodigo = pCnvCodigo;
			this.pCspSeq = pCspSeq;
			this.pQuantidade = pQuantidade;
			this.pPhiSeq = pPhiSeq;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((pCnvCodigo == null) ? 0 : pCnvCodigo.hashCode());
			result = prime * result + ((pCspSeq == null) ? 0 : pCspSeq.hashCode());
			result = prime * result + ((pPhiSeq == null) ? 0 : pPhiSeq.hashCode());
			result = prime * result + ((pQuantidade == null) ? 0 : pQuantidade.hashCode());
			return result;
		}

		@Override
		@SuppressWarnings("PMD.NPathComplexity")
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			RnCthcVerItemSusInner other = (RnCthcVerItemSusInner) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (pCnvCodigo == null) {
				if (other.pCnvCodigo != null) {
					return false;
				}
			} else if (!pCnvCodigo.equals(other.pCnvCodigo)) {
				return false;
			}
			if (pCspSeq == null) {
				if (other.pCspSeq != null) {
					return false;
				}
			} else if (!pCspSeq.equals(other.pCspSeq)) {
				return false;
			}
			if (pPhiSeq == null) {
				if (other.pPhiSeq != null) {
					return false;
				}
			} else if (!pPhiSeq.equals(other.pPhiSeq)) {
				return false;
			}
			if (pQuantidade == null) {
				if (other.pQuantidade != null) {
					return false;
				}
			} else if (!pQuantidade.equals(other.pQuantidade)) {
				return false;
			}
			return true;
		}

		private FatkCthRNCache getOuterType() {
			return FatkCthRNCache.this;
		}
		
	}
	
	protected FatkCthRN getFatkCthRN() {
		return fatkCthRN;
	}
	
}
