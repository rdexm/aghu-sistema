package br.gov.mec.aghu.internacao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMvtoInternacaoDAO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 * ORADB Package AINK_UTIL.
 */
@Stateless
public class InternacaoUtilRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(InternacaoUtilRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinQuartosDAO ainQuartosDAO;

@Inject
private AinLeitosDAO ainLeitosDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AinTiposMvtoInternacaoDAO ainTiposMvtoInternacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6954473399854479287L;

	/**
	 * As mensagens de erro lançadas pelos métodos sobrescritos com o nome "pegaParametro"
	 * devem ser especificadas na enum InternacaoUtilRNExceptionCode.
	 * 
	 * @author Marcelo Tocchetto
	 */
	public enum InternacaoUtilRNExceptionCode implements BusinessExceptionCode {
		AIN_00347, AIN_00659, AIN_00660, AIN_00739, 
		AIN_00422;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}
	}
		
	/**
	 * ORADB Function AINK_UTIL.MODIFICADOS
	 * 
	 * Implementa a função modificados da package aink_util.<br/>
	 * Obs.: A classe dos objetos informados por parâmetro deve implementar o
	 * método equals.
	 * 
	 * @param <T>
	 *            Tipo genérico para indicar que os valores informados por
	 *            parâmetro devem ser do mesmo tipo.
	 * @param newValue
	 *            Valor novo.
	 * @param oldValue
	 *            Valor antigo.
	 * @return Booleano indicando se o valor foi modificado, ou seja, se os
	 *         valores dos parâmetros são diferentes.
	 */
	public <T> Boolean modificados(T newValue, T oldValue) {
		Boolean result = Boolean.FALSE;

		if (newValue != null && oldValue != null) {
			result = !newValue.equals(oldValue);
		} else if (newValue != null || oldValue != null) {
			result = Boolean.TRUE;
		}

		return result;
	}
	
	/**
	 * ORADB Procedure AINK_UTIL.PEGA_PARAMETRO
	 * 
	 * PEGA_PARAMETRO COM VALOR CHARACTER
	 *  
	 *  
	 */
	public String pegaParametro(String nomeParametro, String vlrParametro, InternacaoUtilRNExceptionCode nroMensagem) throws ApplicationBusinessException {
		AghParametrosVO aghParametrosVO = new AghParametrosVO();
		aghParametrosVO.setVlrTexto(vlrParametro);
		
		if (vlrParametro == null) {
			aghParametrosVO.setNome(nomeParametro);
			this.getParametroFacade().getAghpParametro(aghParametrosVO);
			
			if (aghParametrosVO.getMsg() != null) {
				nroMensagem.throwException();
			}
		}
		
		return aghParametrosVO.getVlrTexto();
	}
	
	/**
	 * ORADB Function AINK_UTIL.OBTEM_UNF.
	 * 
	 * Obtem Unidade Funcional.
	 */
	public Short obtemUnf(String newLtoLtoId, Short newQrtNumero, Short newUnfSeq) {
		Short seq = null;
		
		if (newLtoLtoId != null) {
			AinLeitos ainLeitos = getAinLeitosDAO().obterPorChavePrimaria(newLtoLtoId);
			seq = ainLeitos.getQuarto().getUnidadeFuncional().getSeq();
		} else if (newQrtNumero != null) {
			AinQuartos ainQuartos = getAinQuartosDAO().obterPorChavePrimaria(newQrtNumero);
			seq = ainQuartos.getUnidadeFuncional().getSeq();
		} else {
			seq  = newUnfSeq;
		}
		
		return seq;
	}
	
	/**
	 * ORADB Function AINK_UTIL.OBTEM_IND_UNID_EMERGENCIA.
	 * 
	 * Obtem Ind Unid Emergencia.
	 */
	public DominioSimNao obtemIndUnidEmergencia(Short unfSeq) {
		AghUnidadesFuncionais unidadesFuncional = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
		return unidadesFuncional.getIndUnidEmergencia();
	}
	
	/**
	 * ORADB Function AINK_UTIL.OBTEM_CLINICA_ESPECIALIDADE.
	 * 
	 * Obtem Clinica Especialidade.
	 */
	public Integer obtemClinicaEspecialidade(Short espSeq) {
		AghEspecialidades especialidade = this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(espSeq);
		return especialidade.getClinica().getCodigo();
	}
	
	/**
	 * ORADB Function AINK_UTIL.OBTEM_TMI.
	 * 
	 * Obtem TMI.
	 */
	public Integer obtemTmi(String descricao) {
		Integer codigo = getAinTiposMvtoInternacaoDAO().obterCodigoAinTiposMvtoInternacaoPorDescricao(descricao);		
		return codigo;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AinLeitosDAO getAinLeitosDAO(){
		return ainLeitosDAO;
	}
	
	protected AinQuartosDAO getAinQuartosDAO(){
		return ainQuartosDAO;
	}
	
	protected AinTiposMvtoInternacaoDAO getAinTiposMvtoInternacaoDAO(){
		return ainTiposMvtoInternacaoDAO;
	}	
}