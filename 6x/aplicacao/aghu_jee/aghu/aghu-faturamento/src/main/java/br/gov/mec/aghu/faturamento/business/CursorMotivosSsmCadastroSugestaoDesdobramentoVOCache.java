package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobrSsmDAO;
import br.gov.mec.aghu.faturamento.vo.CursorMotivosSsmCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.core.utils.CacheMap;


@RequestScoped
public class CursorMotivosSsmCadastroSugestaoDesdobramentoVOCache implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2560400586659808856L;
	private Map<CursorMotivosSsmCadastroSugestaoDesdobramentoVOCacheInner, List<CursorMotivosSsmCadastroSugestaoDesdobramentoVO>> vos = new CacheMap<CursorMotivosSsmCadastroSugestaoDesdobramentoVOCacheInner, List<CursorMotivosSsmCadastroSugestaoDesdobramentoVO>>(
			5000);
	
	@Inject
	private FatMotivoDesdobrSsmDAO fatMotivoDesdobrSsmDAO;

	public List<CursorMotivosSsmCadastroSugestaoDesdobramentoVO> listarMotivosSsmCadastroSugestaoDesdobramento(Short phoSeq,
			Integer iphSeq, DominioSituacao indSituacao) {
		CursorMotivosSsmCadastroSugestaoDesdobramentoVOCacheInner innerObject = new CursorMotivosSsmCadastroSugestaoDesdobramentoVOCacheInner(
				phoSeq, iphSeq, indSituacao);

		List<CursorMotivosSsmCadastroSugestaoDesdobramentoVO> result = this.vos.get(innerObject);

		if (result == null) {
			result = this.getFatMotivoDesdobrSsmDAO().listarMotivosSsmCadastroSugestaoDesdobramento(phoSeq, iphSeq, DominioSituacao.A);
			this.vos.put(innerObject, result);
		}

		return result;
	}

	private class CursorMotivosSsmCadastroSugestaoDesdobramentoVOCacheInner {

		private Short phoSeq;

		private Integer iphSeq;

		private DominioSituacao indSituacao;

		public CursorMotivosSsmCadastroSugestaoDesdobramentoVOCacheInner(Short phoSeq, Integer iphSeq, DominioSituacao indSituacao) {
			this.phoSeq = phoSeq;
			this.iphSeq = iphSeq;
			this.indSituacao = indSituacao;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((indSituacao == null) ? 0 : indSituacao.hashCode());
			result = prime * result + ((iphSeq == null) ? 0 : iphSeq.hashCode());
			result = prime * result + ((phoSeq == null) ? 0 : phoSeq.hashCode());
			return result;
		}

		@Override
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
			CursorMotivosSsmCadastroSugestaoDesdobramentoVOCacheInner other = (CursorMotivosSsmCadastroSugestaoDesdobramentoVOCacheInner) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (indSituacao != other.indSituacao) {
				return false;
			}
			if (iphSeq == null) {
				if (other.iphSeq != null) {
					return false;
				}
			} else if (!iphSeq.equals(other.iphSeq)) {
				return false;
			}
			if (phoSeq == null) {
				if (other.phoSeq != null) {
					return false;
				}
			} else if (!phoSeq.equals(other.phoSeq)) {
				return false;
			}
			return true;
		}

		private CursorMotivosSsmCadastroSugestaoDesdobramentoVOCache getOuterType() {
			return CursorMotivosSsmCadastroSugestaoDesdobramentoVOCache.this;
		}

	}

	protected FatMotivoDesdobrSsmDAO getFatMotivoDesdobrSsmDAO() {
		return fatMotivoDesdobrSsmDAO;
	}

}
