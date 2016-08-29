package br.gov.mec.aghu.emergencia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamFluxogramaDAO;
import br.gov.mec.aghu.emergencia.dao.MamFluxogramaJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamFluxogramaJn;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Regras de negócio relacionadas à entidade MamFluxograma
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamFluxogramaRN extends BaseBusiness {
	private static final long serialVersionUID = 1357278607603261830L;

	public enum MamFluxogramaRNExceptionCode implements BusinessExceptionCode {
		MAM_MENSAGEM_INCLUSAO_FLUXOGRAMA, //
		MAM_MENSAGEM_ALTERACAO_FLUXOGRAMA, //
		MAM_ERRO_PROTOCOLO_BLOQUEADO, //
		;
	}

	@Inject
	private MamFluxogramaDAO mamFluxogramaDAO;

	@Inject
	private MamFluxogramaJnDAO mamFluxogramaJnDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Insere/Altera um registro de MamFluxograma
	 * 
	 * RN01 de #32285 - Manter cadastro de fluxogramas utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamFluxograma
	 * @throws ApplicationBusinessException
	 */
	public void persistir(MamFluxograma mamFluxograma) throws ApplicationBusinessException {

		// Se a coluna IND_BLOQUEADO do protocolo selecionado no item 2 estiver Ativo, exibir a mensagem de exceção MAM_ERRO_PROTOCOLO_BLOQUEADO e cancelar o processamento.
		if (mamFluxograma.getProtClassifRisco().getIndBloqueado().isAtivo()) {
			throw new ApplicationBusinessException(MamFluxogramaRNExceptionCode.MAM_ERRO_PROTOCOLO_BLOQUEADO, mamFluxograma.getProtClassifRisco()
					.getDescricao());
		}

		if (mamFluxograma.getSeq() == null) {
			// Se estiver no modo de inclusão, realizar o insert I1 e exibir a mensagem MAM_MENSAGEM_INCLUSAO_FLUXOGRAMA caso não ocorra nenhum erro na persistência.
			mamFluxograma.setCriadoEm(new Date());
			mamFluxograma.setSerMatricula(usuario.getMatricula());
			mamFluxograma.setSerVinCodigo(usuario.getVinculo());
			mamFluxogramaDAO.persistir(mamFluxograma);
		} else {
			// Senão, se estiver no modo de alteração, realizar o update U1 e chama RN02 e exibir a mensagem MAM_MENSAGEM_ALTERACAO_FLUXOGRAMA caso não ocorra nenhum erro na
			// persistência.
			MamFluxograma mamFluxogramaOriginal = this.mamFluxogramaDAO.obterOriginal(mamFluxograma);
			this.posUpdate(mamFluxograma, mamFluxogramaOriginal);
			mamFluxogramaDAO.atualizar(mamFluxograma);
		}
	}

	/**
	 * Pos-Update de MamFluxograma
	 * 
	 * @ORADB MAMT_FLX_ARU
	 * 
	 *        RN02 de #32285 - Manter cadastro de fluxogramas utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamFluxograma
	 */
	private void posUpdate(MamFluxograma mamFluxograma, MamFluxograma mamFluxogramaOriginal) {
		if (CoreUtil.modificados(mamFluxograma.getIndSituacao(), mamFluxogramaOriginal.getIndSituacao())
				|| CoreUtil.modificados(mamFluxograma.getDescricao(), mamFluxogramaOriginal.getDescricao())
				|| CoreUtil.modificados(mamFluxograma.getOrdem(), mamFluxogramaOriginal.getOrdem())) {
			this.inserirJournalMamFluxograma(mamFluxogramaOriginal, DominioOperacoesJournal.UPD);
		}

	}

	/**
	 * Insere um registro na journal de MamFluxograma
	 * 
	 * @param mamFluxogramaOriginal
	 * @param operacao
	 */
	private void inserirJournalMamFluxograma(MamFluxograma mamFluxogramaOriginal, DominioOperacoesJournal operacao) {
		MamFluxogramaJn mamFluxogramaJn = BaseJournalFactory.getBaseJournal(operacao, MamFluxogramaJn.class, usuario.getLogin());
		mamFluxogramaJn.setCriadoEm(mamFluxogramaOriginal.getCriadoEm());
		mamFluxogramaJn.setDescricao(mamFluxogramaOriginal.getDescricao());
		mamFluxogramaJn.setIndSituacao(mamFluxogramaOriginal.getIndSituacao());
		mamFluxogramaJn.setOrdem(mamFluxogramaOriginal.getOrdem());
		mamFluxogramaJn.setPcrSeq(mamFluxogramaOriginal.getProtClassifRisco().getSeq());
		mamFluxogramaJn.setSeq(mamFluxogramaOriginal.getSeq());
		mamFluxogramaJn.setSerMatricula(mamFluxogramaOriginal.getSerMatricula());
		mamFluxogramaJn.setSerVinCodigo(mamFluxogramaOriginal.getSerVinCodigo());
		mamFluxogramaJnDAO.persistir(mamFluxogramaJn);
	}

	/**
	 * Pesquisa MamFluxograma ativos por seq ou descricao e/ou seqProtocolo
	 * 
	 * @param param
	 * @param seqProtocolo
	 * @return
	 */
	public List<MamFluxograma> pesquisarFluxogramaAtivoPorProtocolo(String param, Integer seqProtocolo) {
		Integer seq = null;
		String descricao = null;
		if (StringUtils.isNotBlank(param)) {
			if (CoreUtil.isNumeroInteger(param)) {
				seq = Integer.valueOf(param);
			} else {
				descricao = param;
			}
		}
		return mamFluxogramaDAO.pesquisarFluxogramaSeqDescSitProt(seq, descricao, DominioSituacao.A, seqProtocolo, 100);
	}

	/**
	 * Conta MamFluxograma ativos por seq ou descricao e/ou seqProtocolo
	 * 
	 * @param param
	 * @param seqProtocolo
	 * @return
	 */
	public Long pesquisarFluxogramaAtivoPorProtocoloCount(String param, Integer seqProtocolo) {
		Integer seq = null;
		String descricao = null;
		if (StringUtils.isNotBlank(param)) {
			if (CoreUtil.isNumeroInteger(param)) {
				seq = Integer.valueOf(param);
			} else {
				descricao = param;
			}
		}
		return mamFluxogramaDAO.pesquisarFluxogramaSeqDescSitProtCount(seq, descricao, DominioSituacao.A, seqProtocolo);
	};
}
