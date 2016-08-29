package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmObtCausaDireta;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaDiretaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterObtCausaDiretaON extends BaseBusiness {


@EJB
private ManterObtCausaDiretaRN manterObtCausaDiretaRN;

private static final Log LOG = LogFactory.getLog(ManterObtCausaDiretaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmObtCausaDiretaDAO mpmObtCausaDiretaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1613082452606503001L;

	public enum ManterObtCausaDiretaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_EXCEDEU_LIMITE_CAUSA_DIRETA_MORTE,ERRO_ALTERAR_REGISTRO_OBITO_CAUSA_DIRETA,ERRO_EXCLUSAO_REGISTRO_OBITO_CAUSA_DIRETA;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	/**
	 * Cria uma cópia de MPM_OBT_CAUSA_DIRETAS
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 *  
	 */
	public void versionarObtCausaDireta(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
	
		if(altaSumario!=null){
			MpmObtCausaDireta obtCausaDireta = this.getMpmObtCausaDiretaDAO().obterObtCausaDireta(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
			
			if (obtCausaDireta != null) {
				
				MpmObtCausaDireta novoObtCausaDireta = new MpmObtCausaDireta();
				novoObtCausaDireta.setMpmAltaSumarios(altaSumario);
				novoObtCausaDireta.setCid(obtCausaDireta.getCid());
				novoObtCausaDireta.setComplCid(obtCausaDireta.getComplCid());
				novoObtCausaDireta.setDescCid(obtCausaDireta.getDescCid());
				novoObtCausaDireta.setDiagnostico(obtCausaDireta.getDiagnostico());
				novoObtCausaDireta.setIndCarga(obtCausaDireta.getIndCarga());
				novoObtCausaDireta.setIndSituacao(obtCausaDireta.getIndSituacao());
				novoObtCausaDireta.setMpmCidAtendimentos(obtCausaDireta.getMpmCidAtendimentos());
				this.getManterObtCausaDiretaRN().inserirObtCausaDireta(novoObtCausaDireta);
				
			}
		}

		
	}
	
	public MpmObtCausaDireta obterObtCausaDireta(
			Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return getManterObtCausaDiretaRN().obterObtCausaDireta(altanAtdSeq, altanApaSeq, altanAsuSeqp);
	}
	
	
	public String preinserirObtCausaDireta(SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO) throws ApplicationBusinessException {
		String retorno = null;
		Integer apaAtdSeq = sumarioAltaDiagnosticosCidVO.getId().getApaAtdSeq();
		Integer apaSeq = sumarioAltaDiagnosticosCidVO.getId().getApaSeq();
		Short seqp = sumarioAltaDiagnosticosCidVO.getId().getSeqp();
		
		// Pesquisa a ocorrência da Causa Direta da Morte
		MpmObtCausaDireta obtCausaDiretaExistente = this.getMpmObtCausaDiretaDAO().obterObtCausaDireta(apaAtdSeq, apaSeq, seqp);

		// Somente uma ocorrência de A Causa Direta da Morte é permitida
		if (obtCausaDiretaExistente == null) {  // A causa direta da morte NÃO está registrada no banco
			
			MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(apaAtdSeq,apaSeq,seqp);

			if(altaSumario != null){
				
				MpmObtCausaDireta mpmObtCausaDiretaNovo = new MpmObtCausaDireta();		
				mpmObtCausaDiretaNovo.setMpmAltaSumarios(altaSumario);
				mpmObtCausaDiretaNovo.setIndSituacao(DominioSituacao.A);

				if(sumarioAltaDiagnosticosCidVO.getCiaSeq() != null){
					mpmObtCausaDiretaNovo.setIndCarga(DominioSimNao.S);
				}else{
					mpmObtCausaDiretaNovo.setIndCarga(DominioSimNao.N);
				}
				mpmObtCausaDiretaNovo.setDescCid(ManterAltaSumarioON.montaDescricaoCIDComPrimeiraMaiuscula(sumarioAltaDiagnosticosCidVO.getCid().getDescricao(), sumarioAltaDiagnosticosCidVO.getCid().getCodigo()));
				mpmObtCausaDiretaNovo.setCid(sumarioAltaDiagnosticosCidVO.getCid());
				mpmObtCausaDiretaNovo.setComplCid(sumarioAltaDiagnosticosCidVO.getComplementoEditado());
				
				this.getManterObtCausaDiretaRN().inserirObtCausaDireta(mpmObtCausaDiretaNovo);
				retorno = "MENSAGEM_SUCESSO_INCLUSAO_CAUSA_DIRETA_MORTE";
			}
		
		} else{ 
			ManterObtCausaDiretaONExceptionCode.MENSAGEM_EXCEDEU_LIMITE_CAUSA_DIRETA_MORTE.throwException();
			
		}
		return retorno;
		
	}

	public void preatualizarObtCausaDireta(SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO) throws ApplicationBusinessException{
		if(sumarioAltaDiagnosticosCidVO != null){
			
			//MpmObtCausaDireta obtCausaDireta = sumarioAltaDiagnosticosCidVO.getObitoCausaDireta();
			MpmObtCausaDireta  obtCausaDireta = this.getMpmObtCausaDiretaDAO().obterObtCausaDireta(sumarioAltaDiagnosticosCidVO.getObitoCausaDireta().getId().getAsuApaAtdSeq(), sumarioAltaDiagnosticosCidVO.getObitoCausaDireta().getId().getAsuApaSeq(), sumarioAltaDiagnosticosCidVO.getObitoCausaDireta().getId().getAsuSeqp());
						
			obtCausaDireta.setComplCid(sumarioAltaDiagnosticosCidVO.getComplementoEditado());
			
			if(obtCausaDireta.getIndCarga().equals(DominioSimNao.S)){
				ManterObtCausaDiretaONExceptionCode.ERRO_ALTERAR_REGISTRO_OBITO_CAUSA_DIRETA.throwException(); 
			}else{
				getManterObtCausaDiretaRN().atualizarObtCausaDireta(obtCausaDireta);
			}
		}
	}
	
	public void removerObtCausaDireta(SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO) throws ApplicationBusinessException{
		try {			
			MpmObtCausaDireta  mpmObtCausaDireta = this.getMpmObtCausaDiretaDAO().obterObtCausaDireta(sumarioAltaDiagnosticosCidVO.getObitoCausaDireta().getId().getAsuApaAtdSeq(), sumarioAltaDiagnosticosCidVO.getObitoCausaDireta().getId().getAsuApaSeq(), sumarioAltaDiagnosticosCidVO.getObitoCausaDireta().getId().getAsuSeqp());
			this.getManterObtCausaDiretaRN().removerObtCausaDireta(mpmObtCausaDireta);
			
		} catch (ApplicationBusinessException e) {
			ManterObtCausaDiretaONExceptionCode.ERRO_EXCLUSAO_REGISTRO_OBITO_CAUSA_DIRETA.throwException();
			
		}
	}	

	/**
	 * Remove MPM_OBT_CAUSA_DIRETAS
	 * @param altaSumario
	 *  
	 */
	public void removerObtCausaDireta(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		if (altaSumario != null) {
			MpmObtCausaDireta obtCausaDireta = this.getMpmObtCausaDiretaDAO().obterObtCausaDireta(
					altaSumario.getId().getApaAtdSeq(), 
					altaSumario.getId().getApaSeq(), 
					altaSumario.getId().getSeqp(),
					false
			);
			
			if (obtCausaDireta != null) {
				this.getManterObtCausaDiretaRN().removerObtCausaDireta(obtCausaDireta);
			}
		}
	}
	
	public void inserirObtCausaDireta(MpmObtCausaDireta obtCausaDireta) throws ApplicationBusinessException {
		getManterObtCausaDiretaRN().inserirObtCausaDireta(obtCausaDireta);
	}

	protected MpmObtCausaDiretaDAO getMpmObtCausaDiretaDAO() {
		return mpmObtCausaDiretaDAO;
	}
	
	protected ManterObtCausaDiretaRN getManterObtCausaDiretaRN() {
		return manterObtCausaDiretaRN;
	}
	
	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO(){
		return mpmAltaSumarioDAO;
	}

}
