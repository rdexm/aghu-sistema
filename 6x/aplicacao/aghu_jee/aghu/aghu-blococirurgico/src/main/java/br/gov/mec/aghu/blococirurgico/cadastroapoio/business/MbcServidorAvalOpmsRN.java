package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcServidorAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcServidorAvalOpmsJnDAO;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcServidorAvalOpms;
import br.gov.mec.aghu.model.MbcServidorAvalOpmsJn;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcServidorAvalOpmsRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcServidorAvalOpmsRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcServidorAvalOpmsDAO mbcServidorAvalOpmsDAO;
	
	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@Inject
	private MbcServidorAvalOpmsJnDAO mbcServidorAvalOpmsJnDAO;


	private static final long serialVersionUID = 4869594796249357236L;

	public enum ManterServidorAvalRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ERRO_SERV_AVAL_OPME_JA_CADASTRADO, MENSAGEM_ERRO_EXCLUSAO_SERVIDOR_JA_UTILIZOU ,MENSAGEM_ERRO_SERV_AVAL_OPME_ADD_JA_UTILIZADO

	}

	public void persistir(MbcServidorAvalOpms servidor)
			throws ApplicationBusinessException {
		
		boolean isServidorCadastrado = getMbcServidorAvalOpmsDAO().isServidorCadastrado(servidor);
		
		if (isServidorCadastrado) {
			throw new ApplicationBusinessException(
					ManterServidorAvalRNExceptionCode.MENSAGEM_ERRO_SERV_AVAL_OPME_JA_CADASTRADO);
		}
		
		List<MbcRequisicaoOpmes> requisicoesExistentes = mbcRequisicaoOpmesDAO
				.obterRequisicaoPorGrupoAlcadaSeq(servidor.getAlcada().getSeq());

		if (requisicoesExistentes != null && !requisicoesExistentes.isEmpty()) {
			throw new ApplicationBusinessException(
					ManterServidorAvalRNExceptionCode.MENSAGEM_ERRO_SERV_AVAL_OPME_ADD_JA_UTILIZADO);
		}

		int sequencia = getMbcServidorAvalOpmsDAO().maxSequenciaPorGrupoAlcada(servidor) + 1;
		servidor.setSequencia(sequencia);

		getMbcServidorAvalOpmsDAO().persistir(servidor);
		posInserirServidorAval(servidor);
		flush();
	}

	private MbcServidorAvalOpmsDAO getMbcServidorAvalOpmsDAO() {
		return mbcServidorAvalOpmsDAO;
	}
	
	public void removerServidor(MbcServidorAvalOpms elemento) {
		elemento = getMbcServidorAvalOpmsDAO().obterPorChavePrimaria(elemento.getId());
		getMbcServidorAvalOpmsDAO().remover(elemento);
		posRemoverServidorAval(elemento);
		flush();
	}
	
	public void removerServidor(MbcServidorAvalOpms elemento, String loginUsuarioLogado)
			throws ApplicationBusinessException {
		// validar se já foi usado em algum fluxo de requisicao

		List<MbcRequisicaoOpmes> requisicoesExistentes = mbcRequisicaoOpmesDAO.obterRequisicaoPorGrupoAlcadaSeq(
				elemento.getAlcada().getSeq());

		if (requisicoesExistentes != null && !requisicoesExistentes.isEmpty()) {
			throw new ApplicationBusinessException(
					ManterServidorAvalRNExceptionCode.MENSAGEM_ERRO_SERV_AVAL_OPME_JA_CADASTRADO);
		}
		getMbcServidorAvalOpmsDAO().remover(elemento);
		posRemoverServidorAval(elemento);
		getMbcServidorAvalOpmsDAO().flush();
	}
	
	public void ativarDesativarServidor(MbcServidorAvalOpms servidor) {
		//MbcServidorAvalOpms servidorAtivo = buscaServidorNivelAlcadaAtivo(servidor.getAlcada());
		
		/*if(servidorAtivo!=null){
			servidorAtivo.setSituacao(DominioSituacao.I);
			getMbcServidorAvalOpmsDAO().atualizar(servidorAtivo);
		}*/
		getMbcServidorAvalOpmsDAO().atualizar(servidor);
		posAtualizarServidorAval(servidor);
		flush();
	}

	public MbcServidorAvalOpms buscaServidorNivelAlcadaAtivo(MbcAlcadaAvalOpms alcada){
		 return getMbcServidorAvalOpmsDAO().buscaServidorNivelAlcadaAtivo(alcada);
	}
	
	public List<MbcServidorAvalOpms> buscaServidoresPorNivelAlcada(MbcAlcadaAvalOpms nivelAlcada) {
		return getMbcServidorAvalOpmsDAO().buscaServidoresPorNivelAlcada(nivelAlcada);
	}

	public MbcServidorAvalOpms buscaServidoresPorSeq(Short seq) {
		return getMbcServidorAvalOpmsDAO().buscaServidoresPorSeq(seq);
	}
	
	protected void inserirJournal(MbcServidorAvalOpms elemento, DominioOperacoesJournal operacao) {
		MbcServidorAvalOpmsJn journal = BaseJournalFactory.getBaseJournal(operacao,	MbcServidorAvalOpmsJn.class, obterLoginUsuarioLogado());

		journal.setId(elemento.getId());
		journal.setSequencia(elemento.getSequencia());
		journal.setResponsabilidade(elemento.getResponsabilidade());
		journal.setSituacao(elemento.getSituacao());
		journal.setRapServidorCriacao(elemento.getRapServidorCriacao());
		journal.setAlcada(elemento.getAlcada());
		journal.setCriadoEm(elemento.getCriadoEm());
		journal.setModificadoEm(elemento.getModificadoEm());
		journal.setRapServidores(elemento.getRapServidores());
		journal.setRapServidoresModificacao(elemento.getRapServidoresModificacao());
				
		this.mbcServidorAvalOpmsJnDAO.persistir(journal);
	}
	
	protected void posRemoverServidorAval(MbcServidorAvalOpms servidorAval) {
		inserirJournal(servidorAval, DominioOperacoesJournal.DEL);
	}

	protected void posInserirServidorAval(MbcServidorAvalOpms servidorAval) {
		inserirJournal(servidorAval, DominioOperacoesJournal.INS);
	}
	
	protected void posAtualizarServidorAval(MbcServidorAvalOpms servidorAval) {
		inserirJournal(servidorAval, DominioOperacoesJournal.UPD);
	}
}
