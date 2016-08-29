package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.controleinfeccao.dao.MciDuracaoMedidaPreventivasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciDuracaoMedidaPreventivasJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciGrupoProcedRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciPatologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciProcedimentoRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTipoGrupoProcedRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.vo.DuracaoMedidasPreventivasVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventivaJn;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciDuracaoMedidasPreventivasRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(MciDuracaoMedidasPreventivasRN.class);

@Override
@Deprecated
protected Log getLogger() {
	return LOG;
}


@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@Inject
private MciDuracaoMedidaPreventivasDAO mciDuracaoMedidaPreventivasDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private MciDuracaoMedidaPreventivasJnDAO mciDuracaoMedidaPreventivasJnDAO;

@Inject
private MciPatologiaInfeccaoDAO mciPatologiaInfeccaoDAO;

@Inject
private MciTipoGrupoProcedRiscoDAO mciTipoGrupoProcedRiscoDAO;

@Inject
private MciProcedimentoRiscoDAO mciProcedRiscoDAO; 

@Inject
private MciGrupoProcedRiscoDAO mciGrupoProcedRiscoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1262088058722762242L;

	
	private enum ManterDuracaoMedidasExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PERIODO_DURACAO_MEDIDAS, MENSAGEM_DADOS_INCOMPLETOS_DURACAO_MEDIDAS, MENSAGEM_RESTRICAO_EXCLUSAO_DURACAO_MEDIDAS;
	}

	
	// #36265 RN01
	public boolean validaPatologiaAssociadaDuracaoMedidas(List<MciPatologiaInfeccao> patologias) throws ApplicationBusinessException{
		if(patologias.size() > 0){
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < patologias.size(); i++) {
				
				if(patologias.size()-1 == i){
					sb.append(patologias.get(i).getDescricao()).append('.');
				}else{
					sb.append(patologias.get(i).getDescricao()).append(", ");
				}
			}
			throw new ApplicationBusinessException(ManterDuracaoMedidasExceptionCode.MENSAGEM_RESTRICAO_EXCLUSAO_DURACAO_MEDIDAS,sb.toString());
		}
		return true;
	}
	
	// #36265 RN03
	public void validaeInsereDescricaoDuracaoMedidas(MciDuracaoMedidaPreventiva entity) throws ApplicationBusinessException{
		if(entity.getDescricao() == null){
			throw new ApplicationBusinessException(ManterDuracaoMedidasExceptionCode.MENSAGEM_DADOS_INCOMPLETOS_DURACAO_MEDIDAS);	
		}
		entity.setServidor(servidorLogadoFacade.obterServidorLogado());
		getMciDuracaoMedidaPreventivasDAO().inserir(entity);
	}
	
	public void atualizarDuracaoMedidaPreventiva(MciDuracaoMedidaPreventiva entity) throws ApplicationBusinessException{
		entity.setServidorMovimentado(servidorLogadoFacade.obterServidorLogado());
		MciDuracaoMedidaPreventiva entityJournal = getMciDuracaoMedidaPreventivasDAO().obterPorChavePrimaria(entity.getSeq());
		persistirDuracaoMedidasJournal(entityJournal,DominioOperacoesJournal.UPD);
		getMciDuracaoMedidaPreventivasDAO().atualizarDuracao(entity);
	}
	
	// #36265 
	public void persistirDuracaoMedidasJournal(MciDuracaoMedidaPreventiva obj, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MciDuracaoMedidaPreventivaJn journal = BaseJournalFactory.getBaseJournal(operacao, MciDuracaoMedidaPreventivaJn.class, servidorLogado.getUsuario());
		journal.setSeqInt(obj.getSeq().intValue());
		journal.setDescricao(obj.getDescricao());
		journal.setIndSituacao(obj.getSituacao());
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setAlteradoEm(obj.getAlteradoEm());
		
		DuracaoMedidasPreventivasVO vo = getMciDuracaoMedidaPreventivasDAO().obterDuracaoMedidaPreventivaComRelacionamento(obj.getSeq());
		journal.setSerVinCodigo(vo.getVinCodigo());
		journal.setSerMatricula(vo.getMatricula());

		if(vo.getMatriculaMovi() != null){
			journal.setSerVinCodigoMovimentado(vo.getVinCodigoMovi());
			journal.setSerMatriculaMovimentado(vo.getMatriculaMovi());
		}
		getMciDuracaoMedidaPreventivasJnDAO().persistir(journal);		
	}
	
	public boolean validaDiasDuracaoMedidas(Integer parametroDias,Date criadoEm) throws ApplicationBusinessException{
		Date data = new Date(System.currentTimeMillis()); 
		Integer qtd = DateUtil.calcularDiasEntreDatas(criadoEm,data);
		if(qtd > parametroDias){
			throw new ApplicationBusinessException(ManterDuracaoMedidasExceptionCode.MENSAGEM_PERIODO_DURACAO_MEDIDAS);
		}		
		return true;
	}
	
	//#36265
	public void validarRemoverDuracaoMedidas(Short seq, Date criadoEm) throws ApplicationBusinessException{
		//RN 04
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		boolean rn4 = validaDiasDuracaoMedidas(parametro.getVlrNumerico().intValue(),criadoEm);
		
		//RN 01
		List<MciPatologiaInfeccao> patologias = getMciPatologiaInfeccaoDAO().obterPatologiaInfeccoesPorDmpSeq(seq);
		boolean rn1 = validaPatologiaAssociadaDuracaoMedidas(patologias);
		
		if(rn1 && rn4){
			MciDuracaoMedidaPreventiva entity = getMciDuracaoMedidaPreventivasDAO().obterPorChavePrimaria(seq);
			persistirDuracaoMedidasJournal(entity, DominioOperacoesJournal.DEL);
			getMciDuracaoMedidaPreventivasDAO().remover(seq);
		}
	}
	
	protected MciDuracaoMedidaPreventivasDAO getMciDuracaoMedidaPreventivasDAO() {
		return mciDuracaoMedidaPreventivasDAO;
	}
	
	protected MciDuracaoMedidaPreventivasJnDAO getMciDuracaoMedidaPreventivasJnDAO() {
		return mciDuracaoMedidaPreventivasJnDAO;
	}
	
	protected MciPatologiaInfeccaoDAO getMciPatologiaInfeccaoDAO() {
		return mciPatologiaInfeccaoDAO;
	}
	
	protected MciTipoGrupoProcedRiscoDAO getMciTipoGrupoProcedRiscoDAO() {
		return mciTipoGrupoProcedRiscoDAO;
	}
	
	protected MciProcedimentoRiscoDAO getMciProcedRiscoDAO() {
		return mciProcedRiscoDAO;
	}
	
	protected MciGrupoProcedRiscoDAO getMciGrupoProcedRiscoDAO() {
		return mciGrupoProcedRiscoDAO;
	}
	
}

