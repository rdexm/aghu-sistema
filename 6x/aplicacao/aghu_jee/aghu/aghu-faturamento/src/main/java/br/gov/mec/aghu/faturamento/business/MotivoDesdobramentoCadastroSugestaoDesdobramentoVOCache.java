package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.dao.FatMotivoDesdobramentoDAO;
import br.gov.mec.aghu.faturamento.vo.MotivoDesdobramentoCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.core.utils.CacheMap;


@RequestScoped
public class MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCache implements Serializable {
	
	@Inject
	private FatMotivoDesdobramentoDAO fatMotivoDesdobramentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7352212426287194810L;
	private Map<MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCacheInner, List<MotivoDesdobramentoCadastroSugestaoDesdobramentoVO>> vos = new CacheMap<MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCacheInner, List<MotivoDesdobramentoCadastroSugestaoDesdobramentoVO>>(
			5000);

	public List<MotivoDesdobramentoCadastroSugestaoDesdobramentoVO> listaMotivosDesdobramentoCadastroSugestaoDesdobramento(
			Byte tahSeq, Byte clcCodigo, Byte mdsSeq, DominioSituacao situacaoRegistro) {
		MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCacheInner innerObject = new MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCacheInner(
				tahSeq, clcCodigo, mdsSeq, situacaoRegistro);

		List<MotivoDesdobramentoCadastroSugestaoDesdobramentoVO> result = this.vos.get(innerObject);

		if (result == null) {
			result = getFatMotivoDesdobramentoDAO().listaMotivosDesdobramentoCadastroSugestaoDesdobramento(tahSeq, clcCodigo, mdsSeq,
					situacaoRegistro);

			this.vos.put(innerObject, result);
		}

		return result;
	}

	private class MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCacheInner {

		private Byte tahSeq;

		private Byte clcCodigo;

		private Byte mdsSeq;

		private DominioSituacao situacaoRegistro;

		public MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCacheInner(Byte tahSeq, Byte clcCodigo, Byte mdsSeq,
				DominioSituacao situacaoRegistro) {
			this.tahSeq = tahSeq;
			this.clcCodigo = clcCodigo;
			this.mdsSeq = mdsSeq;
			this.situacaoRegistro = situacaoRegistro;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((clcCodigo == null) ? 0 : clcCodigo.hashCode());
			result = prime * result + ((mdsSeq == null) ? 0 : mdsSeq.hashCode());
			result = prime * result + ((situacaoRegistro == null) ? 0 : situacaoRegistro.hashCode());
			result = prime * result + ((tahSeq == null) ? 0 : tahSeq.hashCode());
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
			MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCacheInner other = (MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCacheInner) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (clcCodigo == null) {
				if (other.clcCodigo != null) {
					return false;
				}
			} else if (!clcCodigo.equals(other.clcCodigo)) {
				return false;
			}
			if (mdsSeq == null) {
				if (other.mdsSeq != null) {
					return false;
				}
			} else if (!mdsSeq.equals(other.mdsSeq)) {
				return false;
			}
			if (situacaoRegistro != other.situacaoRegistro) {
				return false;
			}
			if (tahSeq == null) {
				if (other.tahSeq != null) {
					return false;
				}
			} else if (!tahSeq.equals(other.tahSeq)) {
				return false;
			}
			return true;
		}

		private MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCache getOuterType() {
			return MotivoDesdobramentoCadastroSugestaoDesdobramentoVOCache.this;
		}

	}

	protected FatMotivoDesdobramentoDAO getFatMotivoDesdobramentoDAO() {
		return fatMotivoDesdobramentoDAO;
	}

}
