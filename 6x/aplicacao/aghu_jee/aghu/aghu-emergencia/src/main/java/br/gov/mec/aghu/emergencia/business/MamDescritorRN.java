package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.dao.MamDescritorDAO;
import br.gov.mec.aghu.emergencia.dao.MamDescritorJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.DescritorTrgGravidadeVO;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamDescritorJn;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
//import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
//import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Regras de negócio relacionadas à entidade MamDescritor
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamDescritorRN extends BaseBusiness {
	private static final long serialVersionUID = 1357278607603261830L;

	public enum MamDescritorRNExceptionCode implements BusinessExceptionCode {
		MAM_ERRO_PROTOCOLO_BLOQUEADO, //
		MAM_MENSAGEM_INCLUSAO_DESCRITOR, //
		MAM_MENSAGEM_ALTERACAO_DESCRITOR, //
		;
	}

	@Inject
	private MamDescritorDAO mamDescritorDAO;

	@Inject
	private MamDescritorJnDAO mamDescritorJnDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Insere/Altera um registro de MamDescritor
	 * 
	 * RN01 de #32286 - MANTER CADASTRO DE PERGUNTAS UTILIZADAS NOS PROTOCOLOS DE CLASSIFICAÇÃO DE RISCO
	 * 
	 * @param mamDescritor
	 * @throws ApplicationBusinessException
	 */
	public void persistir(MamDescritor mamDescritor) throws ApplicationBusinessException {

		// Se a coluna IND_BLOQUEADO do protocolo selecionado no item 2 estiver Ativo, exibir a mensagem de exceção MAM_ERRO_PROTOCOLO_BLOQUEADO e cancelar o processamento.
		if (mamDescritor.getFluxograma().getProtClassifRisco().getIndBloqueado().isAtivo()) {
			throw new ApplicationBusinessException(MamDescritorRNExceptionCode.MAM_ERRO_PROTOCOLO_BLOQUEADO, mamDescritor.getFluxograma().getProtClassifRisco()
					.getDescricao());
		}

		if (mamDescritor.getSeq() == null) {
			// Se estiver no modo de inclusão, realizar o insert I1 e exibir a mensagem MAM_MENSAGEM_INCLUSAO_FLUXOGRAMA caso não ocorra nenhum erro na persistência.
			mamDescritor.setSerMatricula(usuario.getMatricula());
			mamDescritor.setSerVinCodigo(usuario.getVinculo());
			mamDescritor.setCriadoEm(new Date());
			mamDescritorDAO.persistir(mamDescritor);
		} else {
			// Senão, se estiver no modo de alteração, realizar o update U1, chama RN02 e exibe a mensagem MAM_MENSAGEM_ALTERACAO_DESCRITOR caso não ocorra nenhum erro na
			// persistência.
			MamDescritor mamDescritorOriginal = this.mamDescritorDAO.obterOriginal(mamDescritor);
			mamDescritor.setSerMatricula(usuario.getMatricula());
			mamDescritor.setSerVinCodigo(usuario.getVinculo());
			mamDescritorDAO.atualizar(mamDescritor);
			this.posUpdate(mamDescritor, mamDescritorOriginal);
		}
	}

	/**
	 * Pos-Update de MamDescritor
	 * 
	 * @ORADB MAMT_DCT_ARU
	 * 
	 *        RN02 de #32286 - MANTER CADASTRO DE PERGUNTAS UTILIZADAS NOS PROTOCOLOS DE CLASSIFICAÇÃO DE RISCO
	 * 
	 * @param mamDescritor
	 */
	private void posUpdate(MamDescritor mamDescritor, MamDescritor mamDescritorOriginal) {
		if (CoreUtil.modificados(mamDescritor.getIndSituacao(), mamDescritorOriginal.getIndSituacao())
				|| CoreUtil.modificados(mamDescritor.getDescricao(), mamDescritorOriginal.getDescricao())
				|| CoreUtil.modificados(mamDescritor.getOrdem(), mamDescritorOriginal.getOrdem())
				|| CoreUtil.modificados(mamDescritor.getGravidade(), mamDescritorOriginal.getGravidade())
				|| CoreUtil.modificados(mamDescritor.getIndDtQueixaObgt(), mamDescritorOriginal.getIndDtQueixaObgt())
				|| CoreUtil.modificados(mamDescritor.getIndHrQueixaObgt(), mamDescritorOriginal.getIndHrQueixaObgt())) {
			this.inserirJournalMamDescritor(mamDescritorOriginal, DominioOperacoesJournal.UPD);
		}

	}

	/**
	 * Insere um registro na journal de MamDescritor
	 * 
	 * @param mamDescritorOriginal
	 * @param operacao
	 */
	private void inserirJournalMamDescritor(MamDescritor mamDescritorOriginal, DominioOperacoesJournal operacao) {
		MamDescritorJn mamDescritorJn = BaseJournalFactory.getBaseJournal(operacao, MamDescritorJn.class, usuario.getLogin());
		mamDescritorJn.setCriadoEm(mamDescritorOriginal.getCriadoEm());
		mamDescritorJn.setDescricao(mamDescritorOriginal.getDescricao());
		mamDescritorJn.setIndSituacao(mamDescritorOriginal.getIndSituacao());
		mamDescritorJn.setOrdem(mamDescritorOriginal.getOrdem());
		mamDescritorJn.setFlxSeq(mamDescritorOriginal.getFluxograma().getSeq());
		mamDescritorJn.setGrvSeq(mamDescritorOriginal.getGravidade().getSeq());
		mamDescritorJn.setIndDtQueixaObgt(mamDescritorOriginal.getIndDtQueixaObgt());
		mamDescritorJn.setIndHrQueixaObgt(mamDescritorOriginal.getIndHrQueixaObgt());
		mamDescritorJn.setSeq(mamDescritorOriginal.getSeq());
		mamDescritorJn.setSerMatricula(mamDescritorOriginal.getSerMatricula());
		mamDescritorJn.setSerVinCodigo(mamDescritorOriginal.getSerVinCodigo());
		mamDescritorJnDAO.persistir(mamDescritorJn);
	}

	/**
	 * Cria uma lista de descritores, apontando qual é um MamTrgGravidade e qual não é
	 * 
	 * @param mamFluxograma
	 * @param trgGravidade
	 * @return
	 */
	public List<DescritorTrgGravidadeVO> pesquisarDescritorAtivoPorFluxogramaGravidadeAtivaTriagem(MamFluxograma mamFluxograma, MamTrgGravidade trgGravidade) {
		List<DescritorTrgGravidadeVO> result = new ArrayList<DescritorTrgGravidadeVO>();
		List<MamDescritor> descritores = mamDescritorDAO.pesquisarDescritorAtivoPorFluxogramaGravidadeAtivaTriagem(mamFluxograma);
		if (descritores != null && !descritores.isEmpty()) {
			boolean selecionado = false;
			for (MamDescritor mamDescritor : descritores) {
				DescritorTrgGravidadeVO descritorGravidade = new DescritorTrgGravidadeVO();
				descritorGravidade.setDescritor(mamDescritor);
				descritorGravidade.setHabilitado(true);
				if (trgGravidade != null && mamDescritor.equals(trgGravidade.getMamDescritor())
						&& mamDescritor.getGravidade().equals(trgGravidade.getMamGravidade())) {
					descritorGravidade.setTrgGravidade(true);
					selecionado = true;
				} else {
					descritorGravidade.setTrgGravidade(false);
				}
				result.add(descritorGravidade);
			}

			if (!selecionado) {
				for (DescritorTrgGravidadeVO descritorGravidade : result) {
					descritorGravidade.setTrgGravidade(null);
					descritorGravidade.setHabilitado(false);
				}
				result.get(0).setHabilitado(true);
			}
		}
		return result;
	}
}
