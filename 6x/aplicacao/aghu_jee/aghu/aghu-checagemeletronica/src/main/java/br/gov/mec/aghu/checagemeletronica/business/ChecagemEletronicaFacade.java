package br.gov.mec.aghu.checagemeletronica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.checagemeletronica.dao.EceJustificativaMdtoDAO;
import br.gov.mec.aghu.checagemeletronica.dao.EceOcorrenciaDAO;
import br.gov.mec.aghu.checagemeletronica.dao.EceOrdemDeAdministracaoDAO;
import br.gov.mec.aghu.checagemeletronica.dao.EceOrdemDeAdministracaoHistDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.EceJustificativaMdto;
import br.gov.mec.aghu.model.EceOcorrencia;
import br.gov.mec.aghu.model.EceOrdemDeAdministracao;
import br.gov.mec.aghu.model.EceOrdemDeAdministracaoHist;
import br.gov.mec.aghu.model.VMpmOcorrenciaPrcr;


@Stateless
public class ChecagemEletronicaFacade extends BaseFacade implements IChecagemEletronicaFacade {

@EJB
private EceOcorrenciaRN eceOcorrenciaRN;
@EJB
private EceOrdemXLocalizacaoRN eceOrdemXLocalizacaoRN;
@EJB
private EceJustificativaMdtoRN eceJustificativaMdtoRN;
	
	private static final long serialVersionUID = 8341931086608875042L;

	@Inject
	private EceJustificativaMdtoDAO eceJustificativaMdtoDAO;
	
	@Inject
	private EceOcorrenciaDAO eceOcorrenciaDAO;
	
	@Inject
	private EceOrdemDeAdministracaoDAO eceOrdemDeAdministracaoDAO;

	@Inject
	private EceOrdemDeAdministracaoHistDAO eceOrdemDeAdministracaoHistDAO;	
	
	@Override
	@BypassInactiveModule
	public boolean existeLocalizacao(final Integer newSeq, final Date truncaData, final Short newUnfSeq) {
		return getEceOrdemXLocalizacaoRN().existeLocalizacao(newSeq, truncaData, newUnfSeq);
	}

	@Override
	@BypassInactiveModule
	public void inserirOrdemLocalizacao(Integer newSeq, Date ontem, Short newUnfSeq, Short newQtoNum, String newLeitoId) throws ApplicationBusinessException {
		this.getEceOrdemXLocalizacaoRN().inserirOrdemLocalizacao(newSeq, ontem, newUnfSeq, newQtoNum, newLeitoId);
	}
	
	
	@Override
	@BypassInactiveModule
	public void alterarOrdemLocalizacao(Integer newSeq, Date ontem, Short newUnfSeq, Short newQtoNum, String newLeitoId) throws ApplicationBusinessException {
		this.getEceOrdemXLocalizacaoRN().alterarOrdemLocalizacao(newSeq, ontem, newUnfSeq, newQtoNum, newLeitoId);
	}
	
	@Override
	@BypassInactiveModule
	public void alterarOrdemLocalizacao(final EceOcorrencia eceOcorrencia) {
		this.getEceOcorrenciaRN().alterarOrdemLocalizacao(eceOcorrencia);
	}
	
	@Override
	@BypassInactiveModule
	public List<VMpmOcorrenciaPrcr> buscarMpmOcorrenciaPrcr(final Integer atdSeq, final Date dthrMovimento){
		return getEceOcorrenciaDAO().buscarMpmOcorrenciaPrcr(atdSeq, dthrMovimento);
	}
	
	@Override
	@BypassInactiveModule
	public EceOcorrencia obterPorChavePrimaria(Integer ocoSeq) {
		return getEceOcorrenciaDAO().obterPorChavePrimaria(ocoSeq);
	}
	
	@Override
	@BypassInactiveModule
	public List<EceOrdemDeAdministracao>  buscarOrdemAdmin(Integer atdSeq){
		return getEceOrdemDeAdministracaoDAO(). buscarOrdemAdmin(atdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public List<EceOrdemDeAdministracaoHist>  buscarOrdemAdminHist(Integer atdSeq){
		return getEceOrdemDeAdministracaoHistDAO(). buscarOrdemAdminHist(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<EceJustificativaMdto> pesquisarJustificativasPorSeqDescricaoSituacao(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seq, String descricao) {
		return getEceJustificativaMdtoDAO().pesquisarJustificativasPorSeqDescricaoSituacao(firstResult, maxResult, orderProperty, asc, 
				seq, descricao);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarJustificativasPorSeqDescricaoSituacaoCount(Short seq, String descricao) {
		return getEceJustificativaMdtoDAO().pesquisarJustificativasPorSeqDescricaoSituacaoCount(seq, descricao);
	}

	@Override
	@BypassInactiveModule
	public EceJustificativaMdto obterEceJustificativaMdtoPorSeq(Short seqJustificativaMdto) {
		return this.getEceJustificativaMdtoDAO().obterPorChavePrimaria(seqJustificativaMdto);
	}

	@Override
	@BypassInactiveModule
	public void removerJustificativaMdto(Short seq) throws ApplicationBusinessException{
		getEceJustificativaMdtoDAO().removerPorId(seq);			
	}

	@Override
	@BypassInactiveModule
	public void persistirJustificativaMdto(EceJustificativaMdto justificativaMdto) throws ApplicationBusinessException {
		this.getEceJustificativaMdtoRN().persistirJustificativaMdto(justificativaMdto);
		
	}
	
	protected EceJustificativaMdtoRN getEceJustificativaMdtoRN() {
		return eceJustificativaMdtoRN;
	}

	protected EceOcorrenciaDAO getEceOcorrenciaDAO() {
		return eceOcorrenciaDAO;
	}
	
	protected EceOrdemDeAdministracaoDAO getEceOrdemDeAdministracaoDAO(){
		return eceOrdemDeAdministracaoDAO;
	}
	
	protected EceOrdemDeAdministracaoHistDAO getEceOrdemDeAdministracaoHistDAO(){
		return eceOrdemDeAdministracaoHistDAO;
	}

	protected EceOrdemXLocalizacaoRN getEceOrdemXLocalizacaoRN() {
		return eceOrdemXLocalizacaoRN;
	}

	protected EceOcorrenciaRN getEceOcorrenciaRN() {
		return eceOcorrenciaRN;
	}

	protected EceJustificativaMdtoDAO getEceJustificativaMdtoDAO() {
		return eceJustificativaMdtoDAO;		
	}	
}
