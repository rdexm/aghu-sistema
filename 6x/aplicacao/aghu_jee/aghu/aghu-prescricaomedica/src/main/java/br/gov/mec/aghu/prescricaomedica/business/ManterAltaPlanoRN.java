package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPlanoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPlanoPosAltaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterAltaPlanoRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaPlanoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmAltaPlanoDAO mpmAltaPlanoDAO;

@Inject
private MpmPlanoPosAltaDAO mpmPlanoPosAltaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1857796790486114168L;

	public enum ManterAltaPlanoRNExceptionCode implements BusinessExceptionCode {

		MPM_02645, MPM_02950, MPM_02949, MPM_02643
		, MPM_02644
		, MPM_03038
		;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	public void atualizarAltaPlano(MpmAltaPlano altaPlano) throws ApplicationBusinessException {
		try {
			this.preAtualizarAltaPlano(altaPlano);
			montaDescricaoMotivoComPrimeiraMaiuscula(altaPlano);
			this.getMpmAltaPlanoDAO().atualizar(altaPlano);
			this.getMpmAltaPlanoDAO().flush();
		} catch (ApplicationBusinessException e) {
			logError(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Insere objeto MpmAltaPlano.
	 * 
	 * @param {MpmAltaPlano} altaPlano
	 */
	public void inserirAltaPlano(MpmAltaPlano altaPlano)
			throws ApplicationBusinessException {

			this.preInserirAltaPlano(altaPlano);
			montaDescricaoMotivoComPrimeiraMaiuscula(altaPlano);
			this.getMpmAltaPlanoDAO().persistir(altaPlano);
			this.getMpmAltaPlanoDAO().flush();


	}
	
	private void montaDescricaoMotivoComPrimeiraMaiuscula(MpmAltaPlano altaPlano){
		if(altaPlano != null && altaPlano.getDescPlanoPosAlta()!= null && altaPlano.getDescPlanoPosAlta().trim().length()>1){
			altaPlano.setDescPlanoPosAlta(altaPlano.getDescPlanoPosAlta().trim().substring(0,1).toUpperCase()+altaPlano.getDescPlanoPosAlta().trim().substring(1).toLowerCase());
		}
		
		if(altaPlano != null && altaPlano.getComplPlanoPosAlta()!=null && altaPlano.getComplPlanoPosAlta().trim().length()>1){
			altaPlano.setComplPlanoPosAlta(altaPlano.getComplPlanoPosAlta().trim().substring(0,1).toUpperCase()+altaPlano.getComplPlanoPosAlta().trim().substring(1).toLowerCase());
		}
	}

	/**
	 * ORADB Trigger MPMT_APL_BRI
	 * ORADB Package MPMK_APL_RN.RN_APLP_VER_ALTERA
	 * Descrição:Garantir que o atributo desc plano pos alta só possa ser alterado se a
	 * FK da PLANO POS ALTA também for alterada.
	 * Obrigar que este atributo seja alterado quando a fk da PLANO
	 * POS ALTA for alterada
	 * Operação: INS, UPD
	 * @param {MpmAltaPlano} altaPlano
	 */
	
	protected void preInserirAltaPlano(MpmAltaPlano altaPlano) throws ApplicationBusinessException {
		MpmAltaSumarioDAO daoSumarioAlta = this.getAltaSumarioDAO();
	    MpmAltaSumario altaSumario = daoSumarioAlta.obterPorChavePrimaria(altaPlano.getAltaSumario().getId());
		
	    MpmAltaPlanoDAO dao = this.getMpmAltaPlanoDAO();
	    List<MpmAltaPlano> altaPlanoList = dao.buscaAltaPlanoPorAltaSumario(altaSumario);
	    	
		if(!altaPlanoList.isEmpty()) {
			throw new ApplicationBusinessException(ManterAltaPlanoRNExceptionCode.MPM_03038);
		}
	    
		// Verifica tipo de alta do sumário
		this.getAltaSumarioRN().verificarTipoAlteracao(altaSumario.getId());
		
		// Verifica complemento.
		this.verificarCompl(altaPlano.getMpmPlanoPosAltas().getSeq(), null,	altaPlano.getComplPlanoPosAlta());
		
		if (altaPlano.getMpmPlanoPosAltas() != null) {
			altaPlano.setDescPlanoPosAlta(altaPlano.getMpmPlanoPosAltas().getDescricao());
		} else {
			throw new IllegalArgumentException("Objeto MpmAltaPlano nao esta associado corretamente a MpmPlanoPosAltas");
		}
		
		// Esta verificacao nao faz parte da MPMT_APL_BRI
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaSumario);
	}

	/**
	 * @ORADB Procedure MPMK_APL_RN.RN_APLP_VER_COMPLEM.<br>
	 * 
	 * Operação: INS, UPD<br>
	 * 
	 * Descrição: O atributo compl plano pos alta deve ser obrigatoriamente<br>
     * informado se o PLANO POS ALTA tiver ind exige complemento igual a 'S'.<br>
	 * 
	 * bsoliveira - 01/11/2010
	 * 
	 * @param {Short} novoPlaSeq
	 * @param {Short} oldPlaSeq
	 * @param {String} novoComplPlanoPosAlta
	 * @param {String} oldComplPlanoPosAlta
	 */
	public void verificarCompl(Short novoPlaSeq, Short oldPlaSeq, String novoComplPlanoPosAlta) throws ApplicationBusinessException {
		MpmPlanoPosAlta planoPosAlta = null;

		if (novoPlaSeq != null) {
			planoPosAlta = this.getPlanoPosAltaDAO().obterPlanoPosAltaPeloId(novoPlaSeq.intValue());
		} else {
			planoPosAlta = this.getPlanoPosAltaDAO().obterPlanoPosAltaPeloId(oldPlaSeq.intValue());
		}

		if (planoPosAlta != null) {
			if ((planoPosAlta.getIndExigeComplemento() != null)
					&& planoPosAlta.getIndExigeComplemento().booleanValue()
					&& (StringUtils.isBlank(novoComplPlanoPosAlta))) {
				ManterAltaPlanoRNExceptionCode.MPM_02949.throwException();
			}
			if ((planoPosAlta.getIndExigeComplemento() != null)
					&& !planoPosAlta.getIndExigeComplemento()
							.booleanValue() 
					&& (StringUtils.isNotBlank(novoComplPlanoPosAlta) )) {
				ManterAltaPlanoRNExceptionCode.MPM_02950.throwException();
			}
		} else {
			//MPM-02645:Plano pós alta não encontrado.
			ManterAltaPlanoRNExceptionCode.MPM_02645.throwException();
		}
		
	}
	
	/**
	 * @ORADB Trigger MPMT_APL_BRU
	 *  
	 * @param altaPlano
	 */
	protected void preAtualizarAltaPlano(MpmAltaPlano altaPlano) throws ApplicationBusinessException {
	    MpmAltaSumario altaSumario = this.getAltaSumarioDAO().obterPorChavePrimaria(altaPlano.getAltaSumario().getId());
	    
	    this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaSumario);
			    
	    /*VERIFICA TIPO DE ALTA DO SUMÁRIO DE ALTA*/
	    this.getAltaSumarioRN().verificarTipoAlteracao(altaSumario.getId());
	    
	    Short seqOriginal = null;
	    MpmAltaPlano altaPlanoOriginal = this.getMpmAltaPlanoDAO().obterAltaPlanoOriginal(altaPlano);
	    if (altaPlanoOriginal != null) {
	    	seqOriginal = altaPlanoOriginal.getMpmPlanoPosAltas().getSeq();
	    }
	    
	    /*VERIFICA COMPLEMENTO DO PLANO PÓS ALTA */
		this.verificarCompl(altaPlano.getMpmPlanoPosAltas().getSeq(), seqOriginal, altaPlano.getComplPlanoPosAlta());
		
		if (altaPlano.getMpmPlanoPosAltas() != null) {
			altaPlano.setDescPlanoPosAlta(altaPlano.getMpmPlanoPosAltas().getDescricao());
		} else {
			throw new IllegalArgumentException("Objeto MpmAltaPlano nao esta associado corretamente a MpmPlanoPosAltas");
		}
		
	    /*VERIFICA ALTERAÇÃO DA DESCRIÇÃO DO PLANO PÓS ALTA*/
	    // verificacao implementada nos metodos de pre Insert e pre Update da Entity(MpmAltaPlano).
	    this.verificarAlteracaoPlanoPosAlta(altaPlano, altaPlanoOriginal);
	}
	
	/**
	 * @ORADB  PROCEDURE RN_APLP_VER_ALTERA
	 * 
	 * Operação: UPD<br>
	 * Descrição: Garantir que o atributo desc plano pos alta só possa ser alterado se a
	 * FK da PLANO POS ALTA também for alterada.
	 * Obrigar que este atributo seja alterado quando a fk da PLANO
	 * POS ALTA for alterada.
	 * 
	 * @param altaPlano,altaPlanoOriginal
	 */
	public void verificarAlteracaoPlanoPosAlta(MpmAltaPlano altaPlano,MpmAltaPlano altaPlanoOriginal) throws ApplicationBusinessException {

		if (!altaPlano.getDescPlanoPosAlta().equalsIgnoreCase(altaPlanoOriginal.getDescPlanoPosAlta())
				&& !CoreUtil.modificados(altaPlano.getId(), altaPlanoOriginal.getId())) {
			throw new ApplicationBusinessException (ManterAltaPlanoRNExceptionCode.MPM_02643);
		}

		if (CoreUtil.modificados(altaPlano.getId(), altaPlanoOriginal.getId())
				&& ((altaPlano.getDescPlanoPosAlta() == null) || altaPlano.getDescPlanoPosAlta().equalsIgnoreCase(altaPlanoOriginal.getDescPlanoPosAlta()))) {
			throw new ApplicationBusinessException (ManterAltaPlanoRNExceptionCode.MPM_02644); //MPM-02644
		}

	}
	
	/**
	 * ORADB Trigger MPMT_APL_BRD
	 * 
	 * @param altaPlano
	 */
	protected void preDeletePlanoPosAlta(MpmAltaPlano altaPlano) throws ApplicationBusinessException{
	    MpmAltaSumario altaSumario = this.getAltaSumarioDAO().obterPorChavePrimaria(altaPlano.getAltaSumario().getId());
	    
		// verifica se alta sumário está ativo
	    this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaSumario);
	}
	
	/**
	 * Remove objeto MpmAltaPlano.
	 * 
	 * @param {MpmAltaPlano} altaPlano
	 */
	public void removerPlanoPosAlta(MpmAltaPlano altaPlano) throws ApplicationBusinessException {
	  
		this.preDeletePlanoPosAlta(altaPlano);
	    this.getMpmAltaPlanoDAO().remover(altaPlano);
	    this.getMpmAltaPlanoDAO().flush();
	    
	}
	
	/**
	 * Método que verifica a validação 
	 * do plano da alta do paciente. Deve 
	 * pelo menos ter um registro associado.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public Boolean validarAltaPlano(MpmAltaSumarioId altaSumarioId) {
		List<Long> result = this.getMpmAltaPlanoDAO().listAltaPlano(altaSumarioId);
		
		Long rowCount = 0L;
        if (!result.isEmpty()) {
            rowCount = (Long) result.get(0);
        }
        
		return rowCount > 0;
	}
	

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaSumarioDAO getAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	protected MpmPlanoPosAltaDAO getPlanoPosAltaDAO() {
		return mpmPlanoPosAltaDAO;
	}
	
	private MpmAltaPlanoDAO getMpmAltaPlanoDAO() {
		return mpmAltaPlanoDAO;
	}

}
