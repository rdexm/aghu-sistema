package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghHorariosUnidFuncionalId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterHorarioFuncionamentoUnidadeON extends BaseBusiness {


@EJB
private ManterHorarioFuncionamentoUnidadeRN manterHorarioFuncionamentoUnidadeRN;

private static final Log LOG = LogFactory.getLog(ManterHorarioFuncionamentoUnidadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2396077750807766838L;


	/**
	 * Cria um novo registro
	 * ORADB AGHT_HRF_BRI
	 * @param horariosUnidFuncional
	 * @throws ApplicationBusinessException  
	 */
	public void inserirHorarioFuncionamentoUnidade(AghHorariosUnidFuncional horariosUnidFuncional) throws ApplicationBusinessException {
		
		getManterHorarioFuncionamentoUnidadeRN().inserirHorarioFuncionamentoUnidade(horariosUnidFuncional);
		
	}
	
	/**
	 * Atualiza o registro
	 * @param horariosUnidFuncional
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarHorarioFuncionamentoUnidade(AghHorariosUnidFuncional horariosUnidFuncional) throws ApplicationBusinessException {
		
		getManterHorarioFuncionamentoUnidadeRN().atualizarHorarioFuncionamentoUnidade(horariosUnidFuncional);
		
	}
	
	/**
	 * Remove o registro
	 * @param horariosUnidFuncional
	 *  
	 */
	public void removerHorarioFuncionamentoUnidade(AghHorariosUnidFuncionalId id) throws ApplicationBusinessException {
		getManterHorarioFuncionamentoUnidadeRN().removerHorarioFuncionamentoUnidade(id);
	}
	
	
	private ManterHorarioFuncionamentoUnidadeRN getManterHorarioFuncionamentoUnidadeRN() {
		return manterHorarioFuncionamentoUnidadeRN;
	}

}
