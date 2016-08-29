package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatSaldoUtiDAO;
import br.gov.mec.aghu.faturamento.vo.FatSaldoUTIVO;
import br.gov.mec.aghu.model.FatSaldoUti;
import br.gov.mec.aghu.model.FatSaldoUtiId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class FatSaldoUTIRN extends BaseBusiness {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -513692325002605936L;
	private static final Log LOG = LogFactory.getLog(FatSaldoUTIRN.class);

	private enum FatSaldoUTIRNExceptionCode implements BusinessExceptionCode {
		FAT_00676;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private FatSaldoUtiDAO fatSaldoUtiDAO;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;


	public FatSaldoUTIVO persistirSaldoUti(FatSaldoUTIVO vo, Boolean emEdicao) throws ApplicationBusinessException {
		if (emEdicao != null && emEdicao) {
			FatSaldoUti original = obterOriginal(vo);
			vo = atualizarSaldoUti(vo, original);
		} else {
			vo = inserirSaldoUti(vo);
		}
		return vo;
	}
	
	
	private Integer getCapacidade(FatSaldoUTIVO vo) {
//		RN2
//		Ao atualizar um registro, o valor do campo CAPACIDADE deve ser calculado da seguinte forma:   o Nº de Leitos informado pelo número de dias do mês 
//		relacionado aos campos Mês e Ano informados. Ex.: Se informado 09 no campo Mês, 2014 no campo Ano e 10 no campo Nº de Leitos, então o valor do campo CAPACIDADE será 300.
		Integer diasMes = DateUtil.obterQtdeDiasMes(DateUtil.obterData(vo.getAno(), (vo.getMes()-1), 1));//Mês -1, pois a arquitetura herdou o bug do java calendar.
		Integer capacidade = vo.getNumeroLeitos() * diasMes;
		
		return capacidade;
	}

	private FatSaldoUTIVO inserirSaldoUti(FatSaldoUTIVO vo) throws ApplicationBusinessException {
		FatSaldoUti fatSaldoUti = preInserirSaldoUti(vo);
		getFatSaldoUtiDAO().persistir(fatSaldoUti);
		return vo;
	}
	
	private FatSaldoUTIVO atualizarSaldoUti(FatSaldoUTIVO vo, FatSaldoUti original) throws ApplicationBusinessException {
		original = preAtualizarBancoCapacidade(vo, original);
		getFatSaldoUtiDAO().merge(original);
		
		return vo;
	}

	/**
	 * Método para implementar regras da trigger executada antes da inserção
	 * de <code>Saldo UTI</code>.
	 * @throws ApplicationBusinessException 
	 * 
	 * @ORADB FATT_SUT_BRI
	 * return vo com os dados atualizados
	 * 
	 */
	private FatSaldoUti preInserirSaldoUti(FatSaldoUTIVO vo) throws ApplicationBusinessException {
		verificarExisteID(vo);
		
		FatSaldoUti fatSaldoUti = new FatSaldoUti();
		FatSaldoUtiId id = new FatSaldoUtiId(vo.getMes(), vo.getAno(), vo.getTipoUTI());
		fatSaldoUti.setId(id);
		fatSaldoUti.setNroLeitos(vo.getNumeroLeitos());
		fatSaldoUti.setCapacidade(getCapacidade(vo));
		fatSaldoUti.setSaldo(0);
		fatSaldoUti.setCriadoEm(new Date());
		fatSaldoUti.setCriadoPor(servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		return fatSaldoUti;
	}
	
	
	private void verificarExisteID(FatSaldoUTIVO vo) throws ApplicationBusinessException {
		FatSaldoUti original = obterOriginal(vo);
		if (original != null) {
			throw new ApplicationBusinessException(FatSaldoUTIRNExceptionCode.FAT_00676);
		}
	}


	/**
	 * Método para implementar regras da trigger executada antes da atualização
	 * de <code>Saldo UTI</code>.
	 * 
	 * @ORADB FATT_SUT_BRU
	 * return vo com os dados atualizados
	 * 
	 */
	private FatSaldoUti preAtualizarBancoCapacidade(FatSaldoUTIVO vo, FatSaldoUti original) {
		original.setCapacidade(getCapacidade(vo));
		original.setNroLeitos(vo.getNumeroLeitos());
		original.setAlteradoEm(new Date());
		original.setAlteradoPor(servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		return original;
	}

	private FatSaldoUti obterOriginal(FatSaldoUTIVO vo) {
		FatSaldoUtiId id = new FatSaldoUtiId(vo.getMes(), vo.getAno(), vo.getTipoUTI());
		FatSaldoUti original = getFatSaldoUtiDAO().obterPorChavePrimaria(id);
		return original;
	}

	private FatSaldoUtiDAO getFatSaldoUtiDAO() {
		return fatSaldoUtiDAO;
	}
}
