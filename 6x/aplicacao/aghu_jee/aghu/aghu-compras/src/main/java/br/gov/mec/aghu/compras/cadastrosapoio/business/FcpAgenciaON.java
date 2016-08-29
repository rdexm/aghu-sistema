package br.gov.mec.aghu.compras.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.FcpContaCorrenteEncargoDAO;
import br.gov.mec.aghu.compras.dao.ScoContaCorrenteFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.FcpAgenciaBancoDAO;
import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FcpAgenciaON extends BaseBusiness {

	private static final long serialVersionUID = 2468736352491628082L;
	
	private static final Log LOG = LogFactory.getLog(FcpAgenciaON.class);
	
	@EJB
	private FcpAgenciaBancoRN agenciaBancoRN;
	
	@Inject
	private FcpContaCorrenteEncargoDAO contaCorrenteEncargoDAO;
	
	@Inject
	private FcpAgenciaBancoDAO agenciaBancoDAO;
	
	@Inject
	private ScoContaCorrenteFornecedorDAO contaCorrenteFornecedorDAO; 
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum FcpAgenciaONExceptionCode implements BusinessExceptionCode {
		ERRO_VINCULO_FCP_CONTA_CORRENTE_ENCARGOS, ERRO_PERSISTENCIA_AGENCIA, ERRO_VINCULO_FCP_CONTA_CORRENTE, AGENCIA_COM_ESTE_CODIGO_JA_CADASTRADO;
	}

	public List<FcpAgenciaBanco> pesquisarListaAgencia(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Short codBanco, Integer codigoAgencia) throws BaseException {
		
		return agenciaBancoDAO.listarAgenciaBancoPorCodBancoCodAgencia(firstResult, maxResult, orderProperty, asc, codBanco, codigoAgencia);
	}

	public Long pesquisarCountListaAgencia(Short codBanco, Integer codigoAgencia) throws BaseException {
		return agenciaBancoDAO.countAgenciaBancoPorCodBancoCodAgencia(codBanco, codigoAgencia);
	}

	public void persistirAgencia(FcpAgenciaBanco fcpAgenciaBanco)throws BaseException {
		agenciaBancoRN.persistir(fcpAgenciaBanco);
	}

	public void excluirAgencia(FcpAgenciaBanco fcpAgenciaBanco) throws BaseException {
		notNull(fcpAgenciaBanco, FcpAgenciaONExceptionCode.ERRO_PERSISTENCIA_AGENCIA);
		notNull(fcpAgenciaBanco.getId(), FcpAgenciaONExceptionCode.ERRO_PERSISTENCIA_AGENCIA);
		verificarVinculoContaCorrenteFornecedor(fcpAgenciaBanco);
		verificarVinculoContaCorrenteEncargos(fcpAgenciaBanco);
		agenciaBancoRN.remover(fcpAgenciaBanco.getId());
	}

	private void verificarVinculoContaCorrenteFornecedor(FcpAgenciaBanco fcpAgenciaBanco) throws ApplicationBusinessException {
		if(contaCorrenteFornecedorDAO.countContaCorrenteAssociadasPorAgenciaBanco(fcpAgenciaBanco.getId().getBcoCodigo(), fcpAgenciaBanco.getId().getCodigo()) > 0){
			throw new ApplicationBusinessException(FcpAgenciaONExceptionCode.ERRO_VINCULO_FCP_CONTA_CORRENTE_ENCARGOS) ;
		}
	}

	private void verificarVinculoContaCorrenteEncargos(FcpAgenciaBanco fcpAgenciaBanco) throws ApplicationBusinessException {
		if(contaCorrenteEncargoDAO.countPorAgenciaBanco(fcpAgenciaBanco.getId().getBcoCodigo(), fcpAgenciaBanco.getId().getCodigo()) > 0){
			throw new ApplicationBusinessException(FcpAgenciaONExceptionCode.ERRO_VINCULO_FCP_CONTA_CORRENTE_ENCARGOS) ;
		}
	}

	public void verificarAgenciaBancariaComMesmoCodigo(FcpAgenciaBanco fcpAgenciaBanco) throws ApplicationBusinessException {
		if(agenciaBancoDAO.obterOriginal(fcpAgenciaBanco.getId()) != null){
			throw new ApplicationBusinessException(FcpAgenciaONExceptionCode.AGENCIA_COM_ESTE_CODIGO_JA_CADASTRADO) ;
		}
	}
	
	public void notNull(Object object, BusinessExceptionCode exceptionCode) throws ApplicationBusinessException { 
		try {
			Validate.notNull(object);
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(exceptionCode);
		}
	}
	
}
