package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatBancoCapacidadeDAO;
import br.gov.mec.aghu.faturamento.vo.FatBancoCapacidadeVO;
import br.gov.mec.aghu.model.FatBancoCapacidade;
import br.gov.mec.aghu.model.FatBancoCapacidadeId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class FatBancoCapacidadeRN extends BaseBusiness {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -513692325002605936L;
	private static final Log LOG = LogFactory.getLog(FatBancoCapacidadeRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private FatBancoCapacidadeDAO fatBancoCapacidadeDAO;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;


	/**
	 * Método para implementar regras da trigger executada antes da inserção
	 * de <code>banco capacidades</code>.
	 * 
	 * @ORADB FATT_FBC_BRU
	 * return vo com os dados atualizados
	 * 
	 */
	public FatBancoCapacidadeVO atualizarBancoCapacidade(FatBancoCapacidadeVO vo) throws ApplicationBusinessException {
		//pre-Atualizar
		vo = preAtualizarBancoCapacidade(vo);

		//obter do banco
		FatBancoCapacidade original = obterOriginal(vo);		
		original.setCapacidade(vo.getCapacidade());
		original.setNroLeitos(vo.getNumeroLeitos());
		original.setAlteradoEm(new Date());
		original.setServidorAltera(this.servidorLogadoFacade.obterServidorLogado());
		
		
		getFatBancoCapacidadeDAO().merge(original);
		
		return vo;
	}

	private FatBancoCapacidadeVO preAtualizarBancoCapacidade(FatBancoCapacidadeVO vo) {
//		RN1
//		Ao atualizar um registro, o valor do campo CAPACIDADE deve ser calculado da seguinte forma:   o Nº de Leitos informado pelo número de dias do mês 
//		relacionado aos campos Mês e Ano informados.
//		Ex.: Se informado 09 no campo Mês, 2014 no campo Ano e 10 no campo Nº de Leitos, então o valor do campo CAPACIDADE será 300.
		Integer diasMes = DateUtil.obterQtdeDiasMes(DateUtil.obterData(vo.getAno(), (vo.getMes()-1), 1));//Mês -1, pois a arquitetura herdou o bug do java calendar.
		Integer capacidade = vo.getNumeroLeitos() * diasMes;
		vo.setCapacidade(capacidade);
		
		return vo;
	}

	private FatBancoCapacidade obterOriginal(FatBancoCapacidadeVO vo) {
		FatBancoCapacidadeId id = new FatBancoCapacidadeId(vo.getAno(), vo.getMes(), vo.getClinica());
		FatBancoCapacidade original = getFatBancoCapacidadeDAO().obterPorChavePrimaria(id);
		return original;
	}

	private FatBancoCapacidadeDAO getFatBancoCapacidadeDAO() {
		return fatBancoCapacidadeDAO;
	}
}
