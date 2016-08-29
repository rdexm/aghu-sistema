package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAlcadaAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAlcadaAvalOpmsJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpmsJn;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class EditaNiveisAlcadaAprovacaoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(EditaNiveisAlcadaAprovacaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAlcadaAvalOpmsDAO mbcAlcadaAvalOpmsDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@Inject
	private MbcAlcadaAvalOpmsJnDAO mbcAlcadaAvalOpmsJnDAO;
	
	private static final long serialVersionUID = 2317099895565412647L;

	public enum EditaNiveisAlcadaAprovacaoRNExceptionCode implements
			BusinessExceptionCode {
		ERRO_ALTERAR_NIVEL_ALCADA_VALOR_INCORRETO, MENSAGEM_ERRO_EXCLUSAO_ALCADA_POSSUI_SERVIDORES, MENSAGEM_ERRO_CONSULTA_ALCADA_INFORME_O_CODIGO, MENSAGEM_ERRO_EXCLUSAO_ALCADA_JA_UTILIZADA, MENSAGEM_ERRO_NIVEL_AVAL_OPME_JA_UTILIZADO;
	}

	public void persistirListaNiveisAlcada(List<MbcAlcadaAvalOpms> listaAlcada) throws ApplicationBusinessException {
		
		//verifica se alcada ja foi utilizada anteriormente
		for (MbcAlcadaAvalOpms alcada : listaAlcada) {
			if (alcada.getSeq() != null) {
				List<MbcRequisicaoOpmes> requisicoesExistentes = this.mbcRequisicaoOpmesDAO.obterRequisicaoPorGrupoAlcadaSeq(alcada.getSeq());
				if (requisicoesExistentes != null && !requisicoesExistentes.isEmpty()) {
					throw new ApplicationBusinessException(EditaNiveisAlcadaAprovacaoRNExceptionCode.MENSAGEM_ERRO_NIVEL_AVAL_OPME_JA_UTILIZADO);
				}
			}
		}
		
		this.validaValoresNiveis(listaAlcada);
		for (MbcAlcadaAvalOpms nivelAlcada : listaAlcada) {
			nivelAlcada.setRapServidoresModificacao(servidorLogadoFacade.obterServidorLogado());
			nivelAlcada.setModificadoEm(new Date());
			getMbcAlcadaAvalOpmsDAO().atualizar(nivelAlcada);
			posAtualizarMbcGrupoAlcadaAvalOpms(nivelAlcada);
		}
	}
	
	protected void posAtualizarMbcGrupoAlcadaAvalOpms(MbcAlcadaAvalOpms alcadaAvalOpms) {
		MbcAlcadaAvalOpms oldAlcadaAvalOpms = this.getMbcAlcadaAvalOpmsDAO().obterOriginal(alcadaAvalOpms);

		if(CoreUtil.modificados(alcadaAvalOpms.getValorMinimo(), oldAlcadaAvalOpms.getValorMinimo()) ||
				CoreUtil.modificados(alcadaAvalOpms.getValorMaximo(), oldAlcadaAvalOpms.getValorMaximo()) ||
				CoreUtil.modificados(alcadaAvalOpms.getDescricao(), oldAlcadaAvalOpms.getDescricao())){
			inserirJournal(alcadaAvalOpms, DominioOperacoesJournal.UPD);
		}
	}

	private void validaValoresNiveis(List<MbcAlcadaAvalOpms> listaAlcada)
			throws ApplicationBusinessException {

		BigDecimal valorMaximoAtual = BigDecimal.ZERO;

		final BigDecimal UM_CENTAVO = new BigDecimal("0.01");
														 
		final BigDecimal VALOR_MAXIMO = new BigDecimal("99999999.99");

		for (MbcAlcadaAvalOpms nivelAlcada : listaAlcada) {

			boolean validaValorMaximo = (VALOR_MAXIMO.subtract(
					nivelAlcada.getValorMinimo()).compareTo(BigDecimal.ZERO) > 0 && VALOR_MAXIMO
					.subtract(nivelAlcada.getValorMaximo()).compareTo(
							BigDecimal.ZERO) >= 0);
			
			int indexAlcadaAtual = listaAlcada.indexOf(nivelAlcada);

			boolean validarRegraUmCentavo = Boolean.TRUE;

			if ((listaAlcada.size() > 1) && (indexAlcadaAtual > 0)) {
				// Avalia se a propriedade Valor Máximo informado no nível de
				// Alçada(n-1) é menor que a propriedade Valor Mínimo em um
				// Centavo
				validarRegraUmCentavo = nivelAlcada
						.getValorMinimo()
						.subtract(
								listaAlcada.get(indexAlcadaAtual - 1)
										.getValorMaximo()).equals(UM_CENTAVO);
			}
			if (!validaValorMaximo
					|| valorMaximoAtual.compareTo(nivelAlcada.getValorMaximo()) >= 0
					|| valorMaximoAtual.compareTo(nivelAlcada.getValorMinimo()) >= 0
					|| nivelAlcada.getValorMaximo()
							.subtract(nivelAlcada.getValorMinimo())
							.doubleValue() <= 0
					|| nivelAlcada.getValorMinimo().compareTo(valorMaximoAtual) <= 0
					|| !validarRegraUmCentavo) {

				throw new ApplicationBusinessException(
						EditaNiveisAlcadaAprovacaoRNExceptionCode.ERRO_ALTERAR_NIVEL_ALCADA_VALOR_INCORRETO);
			}
			valorMaximoAtual = nivelAlcada.getValorMaximo();
		}
	}

	private MbcAlcadaAvalOpmsDAO getMbcAlcadaAvalOpmsDAO() {
		return mbcAlcadaAvalOpmsDAO;
	}

	public void removerNiveisAlcada(Short seqNivelExcluir)
			throws ApplicationBusinessException {
		MbcAlcadaAvalOpms nivel = getMbcAlcadaAvalOpmsDAO().buscaNivelAlcada(
				seqNivelExcluir);

		if (this.existeVinculacaoProcessoAprovacaoAlcada(nivel)) {
			throw new ApplicationBusinessException(
					EditaNiveisAlcadaAprovacaoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_ALCADA_JA_UTILIZADA);
		}

		if (this.existeServidorAssociado(nivel.getSeq())) {
			throw new ApplicationBusinessException(
					EditaNiveisAlcadaAprovacaoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_ALCADA_POSSUI_SERVIDORES);
		}

		getMbcAlcadaAvalOpmsDAO().remover(nivel);
		posRemoverMbcAlcada(nivel);
		getMbcAlcadaAvalOpmsDAO().flush();
	}
	
	protected void posRemoverMbcAlcada(MbcAlcadaAvalOpms alcadaAvalOpms) {
		inserirJournal(alcadaAvalOpms, DominioOperacoesJournal.DEL);
	}

	private boolean existeServidorAssociado(Short seqNivelAlcada) {
		return getMbcAlcadaAvalOpmsDAO().verificaExistenciaDeServidoresAssociadosAoNivelDeAlcada(seqNivelAlcada);
	}

	private boolean existeVinculacaoProcessoAprovacaoAlcada(MbcAlcadaAvalOpms nivel) {
		List<MbcRequisicaoOpmes> requisicoesExistentes = this.mbcRequisicaoOpmesDAO.obterRequisicaoPorGrupoAlcadaSeq(nivel.getGrupoAlcada().getSeq());
		if (requisicoesExistentes != null && !requisicoesExistentes.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public MbcAlcadaAvalOpms buscaNivelAlcada(MbcAlcadaAvalOpms nivelAlcada)
			throws ApplicationBusinessException {
		if (nivelAlcada == null || nivelAlcada.getSeq() == null) {
			throw new ApplicationBusinessException(
					EditaNiveisAlcadaAprovacaoRNExceptionCode.MENSAGEM_ERRO_CONSULTA_ALCADA_INFORME_O_CODIGO);
		}
		return getMbcAlcadaAvalOpmsDAO().buscaNivelAlcada(nivelAlcada.getSeq());
	}
	
	protected void inserirJournal(MbcAlcadaAvalOpms elemento, DominioOperacoesJournal operacao) {
		MbcAlcadaAvalOpmsJn journal = BaseJournalFactory.getBaseJournal(operacao,	MbcAlcadaAvalOpmsJn.class, obterLoginUsuarioLogado());

		journal.setSeq(elemento.getSeq());
		journal.setGrupoAlcada(elemento.getGrupoAlcada());
		journal.setNivelAlcada(elemento.getNivelAlcada());
		journal.setDescricao(elemento.getDescricao());
		journal.setValorMinimo(elemento.getValorMinimo());
		journal.setValorMaximo(elemento.getValorMaximo());
		journal.setCriadoEm(elemento.getCriadoEm());
		journal.setModificadoEm(elemento.getModificadoEm());
		journal.setRapServidores(elemento.getRapServidores());
		journal.setRapServidoresModificacao(elemento.getRapServidoresModificacao());
		
		this.mbcAlcadaAvalOpmsJnDAO.persistir(journal);
	}
	
}
