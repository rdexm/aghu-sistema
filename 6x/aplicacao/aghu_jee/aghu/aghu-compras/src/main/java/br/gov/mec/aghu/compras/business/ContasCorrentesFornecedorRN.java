package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoContaCorrenteFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.vo.CadastroContasCorrentesFornecedorVO;
import br.gov.mec.aghu.compras.vo.ContasCorrentesFornecedorVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoContaCorrenteFornecedor;
import br.gov.mec.aghu.model.ScoContaCorrenteFornecedorId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ContasCorrentesFornecedorRN extends BaseBusiness {

	private static final long serialVersionUID = -7620204295459637684L;

	public enum ContasCorrentesFornecedorRNExceptionCode implements BusinessExceptionCode {
		CC_FORNECEDOR_M7, CC_FORNECEDOR_M8, CC_FORNECEDOR_M9;
	}
	
	private static final Log LOG = LogFactory.getLog(ContasCorrentesFornecedorRN.class);
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;
	@Inject
	private ScoContaCorrenteFornecedorDAO scoContaCorrenteFornecedorDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public List<ContasCorrentesFornecedorVO> buscarDadosContasCorrentesFornecedor(CadastroContasCorrentesFornecedorVO filtro) {
		return getScoFornecedorDAO().buscarDadosContasCorrentesFornecedor(filtro);
	}
	
	public ScoContaCorrenteFornecedor montarContaCorrenteFornecedor(CadastroContasCorrentesFornecedorVO filtro) {
		ScoContaCorrenteFornecedor ccFornecedor = new ScoContaCorrenteFornecedor();
		ccFornecedor.setId(montarId(filtro));
		ccFornecedor.setIndPreferencial(getIndPreferencialValue(filtro.isPreferencial()));
		ccFornecedor.setVersion(0);
		return ccFornecedor;
	}

	private ScoContaCorrenteFornecedorId montarId(CadastroContasCorrentesFornecedorVO filtro) {
		ScoContaCorrenteFornecedorId id = new ScoContaCorrenteFornecedorId();
		id.setFrnNumero(filtro.getFornecedor().getNumero());
		id.setAgbBcoCodigo(Integer.valueOf(filtro.getAgenciaBanco().getFcpBanco().getCodigo().toString()));
		id.setAgbCodigo(Integer.valueOf(filtro.getAgenciaBanco().getId().getCodigo().toString()));
		id.setContaCorrente(filtro.getNumeroConta());
		return id;
	}
	
	private DominioSimNao getIndPreferencialValue(boolean preferencial) {
		return preferencial ? DominioSimNao.S : DominioSimNao.N;
	}
	
	public void verificaRegistrosExistentes(List<ContasCorrentesFornecedorVO> validationData) throws ApplicationBusinessException {
		if(validationData.size() > 0){
			throw new ApplicationBusinessException(ContasCorrentesFornecedorRNExceptionCode.CC_FORNECEDOR_M7);
		}
	}
	
	public void deletarContaCorrenteFornecedor(ContasCorrentesFornecedorVO item) throws BaseException {
		ScoContaCorrenteFornecedor entity = getScoContaCorrenteFornecedorDAO().obterPorChavePrimaria(montarContaCorrente(item).getId());
		getScoContaCorrenteFornecedorDAO().remover(entity);		
	}
	
	private ScoContaCorrenteFornecedor montarContaCorrente(ContasCorrentesFornecedorVO item) {
		ScoContaCorrenteFornecedor entityToDelete = new ScoContaCorrenteFornecedor();
		entityToDelete.setId(montarId(item));
		return entityToDelete;
	}

	private ScoContaCorrenteFornecedorId montarId(ContasCorrentesFornecedorVO item) {
		ScoContaCorrenteFornecedorId id = new ScoContaCorrenteFornecedorId();
		id.setAgbBcoCodigo(item.getBancoCodigo());
		id.setAgbCodigo(item.getAgenciaCodigo());
		id.setFrnNumero(item.getFornecedorNumero());
		id.setContaCorrente(item.getContaCorrente());
		return id;
	}
	
	public void atualizarContaCorrenteFornecedor(ContasCorrentesFornecedorVO item) throws BaseException {
		ScoContaCorrenteFornecedor entity = getScoContaCorrenteFornecedorDAO().obterPorChavePrimaria(montarContaCorrente(item).getId());
		entity.setIndPreferencial(montarPreferencia(item.obterSelectedPref()));
		getScoContaCorrenteFornecedorDAO().persistir(entity);	
	}

	private DominioSimNao montarPreferencia(boolean obterSelectedPref) {
		if(obterSelectedPref){
			return DominioSimNao.S;
		}else{
			return DominioSimNao.N;
		}
	}
	
	public void verificaContaPreferencialParaFornecedor(Integer numeroFornecedor, DominioSimNao preferencial, boolean isUpdate) throws ApplicationBusinessException {
		if(existeContaPreferencialParaFornecedor(numeroFornecedor, preferencial, isUpdate)){
			lancarException(isUpdate);
		}
	}
	
	private void lancarException(boolean isUpdate) throws ApplicationBusinessException {
		if(isUpdate){
			throw new ApplicationBusinessException(ContasCorrentesFornecedorRNExceptionCode.CC_FORNECEDOR_M9);
		}else{
			throw new ApplicationBusinessException(ContasCorrentesFornecedorRNExceptionCode.CC_FORNECEDOR_M8);
		}
	}

	private boolean existeContaPreferencialParaFornecedor(Integer numeroFornecedor, DominioSimNao preferencial, boolean isUpdate){
		return getScoContaCorrenteFornecedorDAO().existeContaPreferencialParaFornecedor(numeroFornecedor, preferencial, isUpdate);
	}
	
	private ScoFornecedorDAO getScoFornecedorDAO() {
		return scoFornecedorDAO;
	}
	
	private ScoContaCorrenteFornecedorDAO getScoContaCorrenteFornecedorDAO() {
		return scoContaCorrenteFornecedorDAO;
	}

}
