package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PtmEdificacao;
import br.gov.mec.aghu.model.PtmEdificacaoJN;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipLogradourosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmEdificacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmEdificacaoJNDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PtmEdificacaoRN extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 5858303489514850167L;
	
	private static final Log LOG = LogFactory.getLog(PtmEdificacaoRN.class);
	
	@Inject 
	private PtmEdificacaoDAO ptmEdificacaoDAO;

	@Inject 
	private PtmEdificacaoJNDAO ptmEdificacaoJNDAO;
	
	@Inject 
	private AipLogradourosDAO aipLogradourosDAO;
	
	@Inject 
	private PtmBemPermanentesDAO ptmBemPermanentesDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public void gravarEdificacao(DominioSituacao situacao, String nome, String descricao, Long bpeSeq,
			Integer lgrSeq, Integer numero, String complemento, double latitude, double longitude) throws ApplicationBusinessException{
		
		PtmEdificacao ptmEdificacao = new PtmEdificacao();
		ptmEdificacao.setSituacao(situacao);
		ptmEdificacao.setNome(nome);
		ptmEdificacao.setDescricao(descricao);
		ptmEdificacao.setPtmBemPermanentes(this.ptmBemPermanentesDAO.obterPorChavePrimaria(bpeSeq));
		ptmEdificacao.setAipLogradouros(this.aipLogradourosDAO.obterPorChavePrimaria(lgrSeq));
		ptmEdificacao.setNumero(numero);
		ptmEdificacao.setComplemento(complemento);
		ptmEdificacao.setLatitude(latitude);
		ptmEdificacao.setLongitude(longitude);
		ptmEdificacao.setDtCriacao(new Date());
		
		RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		ptmEdificacao.setServidor(servidor);
		ptmEdificacaoDAO.persistir(ptmEdificacao);	
		
		inserirPtmEdificacaoJN(ptmEdificacao, servidor, DominioOperacoesJournal.INS);
	}
	
	public void alterarEdificacao(DominioSituacao situacao, String nome, String descricao, Long bpeSeq,
			Integer lgrSeq, Integer numero, String complemento, double latitude, double longitude, Integer edfSeq) throws ApplicationBusinessException{
		PtmEdificacao ptmEdificacao = this.ptmEdificacaoDAO.obterPorChavePrimaria(edfSeq);
		
		ptmEdificacao.setSituacao(situacao);
		ptmEdificacao.setNome(nome);
		ptmEdificacao.setDescricao(descricao);
		ptmEdificacao.setPtmBemPermanentes(this.ptmBemPermanentesDAO.obterPorChavePrimaria(bpeSeq));
		ptmEdificacao.setAipLogradouros(this.aipLogradourosDAO.obterPorChavePrimaria(lgrSeq));
		ptmEdificacao.setNumero(numero);
		ptmEdificacao.setComplemento(complemento);
		ptmEdificacao.setLatitude(latitude);
		ptmEdificacao.setLongitude(longitude);
		ptmEdificacao.setDtAlteradoEm(new Date());
		
		RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		ptmEdificacao.setServidorAlterado(servidor);
		
		ptmEdificacaoDAO.merge(ptmEdificacao);

		inserirPtmEdificacaoJN(ptmEdificacao, servidor, DominioOperacoesJournal.UPD);
	}
	
	private void inserirPtmEdificacaoJN(PtmEdificacao registro, RapServidores servidor, DominioOperacoesJournal doj){
		
		PtmEdificacaoJN jn = 	BaseJournalFactory.getBaseJournal(
				doj, PtmEdificacaoJN.class, servidor != null ? servidor.getUsuario() : null);
		
		jn.setSeq(registro.getSeq());
		jn.setSituacao(registro.getSituacao());
		jn.setNome(registro.getNome());
		jn.setDescricao(registro.getDescricao());
		jn.setDtAlteradoEm(registro.getDtAlteradoEm());
		jn.setDtCriacao(registro.getDtCriacao());

		if(registro.getServidor() != null){
			jn.setSerMatricula(registro.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(registro.getServidor().getId().getVinCodigo());
		}

		if(registro.getServidorAlterado() != null){
			jn.setSerMatriculaAlterado(registro.getServidorAlterado().getId().getMatricula());
			jn.setSerVinCodigoAlterado(registro.getServidorAlterado().getId().getVinCodigo());
		}
		
		if(registro.getPtmBemPermanentes() != null){
			jn.setBpeSeq(registro.getPtmBemPermanentes().getSeq());
		}

		if(registro.getAipLogradouros() != null){
			jn.setLgrCodigo(registro.getAipLogradouros().getCodigo());
		}
		
		jn.setNumero(registro.getNumero());
		jn.setComplemento(registro.getComplemento());
		jn.setLongitude(registro.getLongitude());
		jn.setLatitude(registro.getLatitude());
		jn.setVersion(registro.getVersion());
		
		ptmEdificacaoJNDAO.persistir(jn);
		
	}
}
