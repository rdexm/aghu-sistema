package br.gov.mec.aghu.emergencia.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendeEspDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendemDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendemJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidXExameDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidXMedicacaoDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidXSinalVitalDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.MamUnidAtendemJn;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras de negócio relacionadas ao Usuário.
 * 
 * @author geraldo
 * 
 */
@Stateless
public class MamUnidAtendemRN extends BaseBusiness {
	private static final String UNID_ATENDEM = "Unid Atendem";

	private static final long serialVersionUID = -3423984755101821178L;

	
	public enum MamUnidAtendemRNExceptionCode implements BusinessExceptionCode {		
		MENSAGEM_EMERG_ERRO_UNIDADE_JA_CADASTRADA,
		MENSAGEM_EMERG_ERRO_PARAMETRO_UNIDADE_PAI_EMERGENCIA,
		MENSAGEM_EMERG_ERRO_OFG_00005,
		OPTIMISTIC_LOCK
	}
	
	@Inject
	private MamUnidAtendemDAO mamUnidAtendemDAO;
	
	@Inject
	private MamUnidAtendemJnDAO mamUnidAtendemJnDAO;


	/*@Inject
	private RegistroColaboradorService registroColaboradorService;*/
	
	@Inject @QualificadorUsuario
	private Usuario usuario;

	@Inject
	private MamUnidAtendeEspDAO mamUnidAtendeEspDAO;
	
	@Inject
	private MamUnidXMedicacaoDAO mamUnidXMedicacaoDAO;
	
	@Inject
	private MamUnidXExameDAO mamUnidXExameDAO;
	
	@Inject
	private MamUnidXSinalVitalDAO mamUnidXSinalVitalDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@EJB
	private IParametroFacade parametroFacade; 
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

		
	public Boolean verificaExisteUnidadeFuncionalSeq(Short seq){
		return (this.mamUnidAtendemDAO.pesquisarUnidadesFuncionaisEmergenciaCount(seq, null, null) > 0);
	}
	
	/***
	 * @ORADB  MAM_UNID_ATENDEM.MAMT_UAE_BRI
	 * @param mamUnidAtendem
	 * @param hostName
	 * @throws ApplicationBusinessException
	 */
	public void inserir(MamUnidAtendem mamUnidAtendem, String hostName) throws ApplicationBusinessException{
		
		if (verificaExisteUnidadeFuncionalSeq(mamUnidAtendem.getUnfSeq())){
			throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.MENSAGEM_EMERG_ERRO_UNIDADE_JA_CADASTRADA);
		}		
						
		mamUnidAtendem.setCriadoEm(new Date());		
		mamUnidAtendem.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		
		mamUnidAtendem.setMicNome(hostName);
		
		if (mamUnidAtendem.getMamUnidAtendem() == null &&
		    !mamUnidAtendem.getIndRecepcao()){
			
			BigDecimal valParametro=null;
			valParametro = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_AGHU_UNIDADE_PAI_EMERGENCIA.toString(), "vlrNumerico");
			if (valParametro == null){
				throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.MENSAGEM_EMERG_ERRO_PARAMETRO_UNIDADE_PAI_EMERGENCIA);
			}
			else {
				MamUnidAtendem mamUnidAtendemPai = this.mamUnidAtendemDAO.obterPorChavePrimaria(valParametro.shortValue());
				mamUnidAtendem.setMamUnidAtendem(mamUnidAtendemPai);
			}			
			
		}
		
		this.mamUnidAtendemDAO.persistir(mamUnidAtendem);		
		
	}
	
	
	/**
	 * @ORADB  MAM_UNID_ATENDEM.MAMT_UAE_ARU
	 * @param mamUnidAtendem
	 * @throws ApplicationBusinessException 
	 */
	public void alterar(MamUnidAtendem mamUnidAtendem, String hostName) throws ApplicationBusinessException {

		try {
			MamUnidAtendem mamUnidAtendemOriginal = this.mamUnidAtendemDAO.obterOriginal(mamUnidAtendem.getSeq());

			if(mamUnidAtendemOriginal == null) {
				throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.OPTIMISTIC_LOCK);
			}		
			
			mamUnidAtendem.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
			mamUnidAtendem.setMicNome(hostName);

			if (CoreUtil.modificados(mamUnidAtendem.getDescricao(), mamUnidAtendemOriginal.getDescricao())
					|| CoreUtil.modificados(mamUnidAtendem.getIndSituacao(), mamUnidAtendemOriginal.getIndSituacao())
					|| CoreUtil.modificados(mamUnidAtendem.getIndTriagem(), mamUnidAtendemOriginal.getIndTriagem())
					|| CoreUtil.modificados(mamUnidAtendem.getIndDivideIdade(), mamUnidAtendemOriginal.getIndDivideIdade())
					|| CoreUtil.modificados(mamUnidAtendem.getIndRecepcao(), mamUnidAtendemOriginal.getIndRecepcao())
					|| CoreUtil.modificados(mamUnidAtendem.getMicNome(), mamUnidAtendemOriginal.getMicNome())
					|| CoreUtil.modificados(mamUnidAtendem.getRapServidores(), mamUnidAtendemOriginal.getRapServidores())
					|| CoreUtil.modificados(mamUnidAtendem.getMamUnidAtendem(), mamUnidAtendemOriginal.getMamUnidAtendem())
					|| CoreUtil.modificados(mamUnidAtendem.getMamProtClassifRisco(), mamUnidAtendemOriginal.getMamProtClassifRisco())) {

				this.inserirJournalUnidAtendem(mamUnidAtendemOriginal, DominioOperacoesJournal.UPD);
			}
			mamUnidAtendemDAO.merge(mamUnidAtendem);

		} catch (BaseOptimisticLockException e) {
			throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.OPTIMISTIC_LOCK);
		}

	}
	 
	public void excluir(Short unfSeq) throws ApplicationBusinessException {	  	
	  		
	  		MamUnidAtendem mamUnidAtendemOriginal = this.mamUnidAtendemDAO.obterPorChavePrimaria(unfSeq);
	  		
	  		/*** consulta C5 Sinais Vitais***/
	  		if (this.mamUnidXSinalVitalDAO.existeUnidXSinalPorUnidadeAtendem(mamUnidAtendemOriginal)) {
	  			throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.MENSAGEM_EMERG_ERRO_OFG_00005,UNID_ATENDEM,"Unid x Sinais Vitais");
	  		}
	  		/*** consulta C6 Especialidades ***/
	  		if (this.mamUnidAtendeEspDAO.existeUnidAtendeEspPorUnidadeAtendem(mamUnidAtendemOriginal.getUnfSeq())) {
	  			throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.MENSAGEM_EMERG_ERRO_OFG_00005,UNID_ATENDEM,"Unid Atende Esps");
	  		}
	  		
	  		/*** consulta C7 Medicacoes ***/
	  		if (this.mamUnidXMedicacaoDAO.existeUnidXMedicacaoPorUnidadeAtendem(mamUnidAtendemOriginal)) {
	  			throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.MENSAGEM_EMERG_ERRO_OFG_00005,UNID_ATENDEM,"Unid X Medicações");
	  		}
	  		
	  		/*** consulta C8 Exames ***/
	  		if (this.mamUnidXExameDAO.existeUnidXExamePorUnidadeAtendem(mamUnidAtendemOriginal)) {
	  			throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.MENSAGEM_EMERG_ERRO_OFG_00005,UNID_ATENDEM,"Unid X Exames");
	  		}
	  		
	  		this.inserirJournalUnidAtendem(mamUnidAtendemOriginal, DominioOperacoesJournal.DEL);	  		
	  		
	  		this.mamUnidAtendemDAO.remover(mamUnidAtendemOriginal);
	  		
	 }
	
	public void inserirJournalUnidAtendem(MamUnidAtendem mamUnidAtendemOriginal, DominioOperacoesJournal operacao) {
		    MamUnidAtendemJn mamUnidAtendemJn = new MamUnidAtendemJn();
			mamUnidAtendemJn.setNomeUsuario(usuario.getLogin());
			mamUnidAtendemJn.setOperacao(operacao);
			mamUnidAtendemJn.setUnfSeq(mamUnidAtendemOriginal.getUnfSeq());
			mamUnidAtendemJn.setDescricao(mamUnidAtendemOriginal.getDescricao());
			mamUnidAtendemJn.setCriadoEm(mamUnidAtendemOriginal.getCriadoEm());
			mamUnidAtendemJn.setIndSituacao(mamUnidAtendemOriginal.getIndSituacao());
			mamUnidAtendemJn.setIndTriagem(mamUnidAtendemOriginal.getIndTriagem());
			mamUnidAtendemJn.setIndDivideIdade(mamUnidAtendemOriginal.getIndDivideIdade());
			mamUnidAtendemJn.setIndRecepcao(mamUnidAtendemOriginal.getIndRecepcao());
			mamUnidAtendemJn.setMicNome(mamUnidAtendemOriginal.getMicNome());
			mamUnidAtendemJn.setSerMatricula(mamUnidAtendemOriginal.getRapServidores().getId().getMatricula());
			mamUnidAtendemJn.setSerVinCodigo(mamUnidAtendemOriginal.getRapServidores().getId().getVinCodigo());
			mamUnidAtendemJn.setMamUnidAtendem(mamUnidAtendemOriginal.getMamUnidAtendem());
			mamUnidAtendemJn.setIndObrOrgPaciente(mamUnidAtendemOriginal.getIndObrOrgPaciente());
			if(mamUnidAtendemOriginal.getMamProtClassifRisco() != null) {
				mamUnidAtendemJn.setPcrSeq(mamUnidAtendemOriginal.getMamProtClassifRisco().getSeq());
			} else {
				mamUnidAtendemJn.setPcrSeq(null);
			}
			mamUnidAtendemJn.setIndMenorResponsavel(mamUnidAtendemOriginal.getIndMenorResponsavel());
			mamUnidAtendemJnDAO.persistir(mamUnidAtendemJn);
	}
}
