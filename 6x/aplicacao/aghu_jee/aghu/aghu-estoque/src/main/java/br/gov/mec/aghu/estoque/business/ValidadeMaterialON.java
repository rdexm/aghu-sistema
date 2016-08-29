package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteFornecedorDAO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceLoteFornecedor;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ValidadeMaterialON extends BaseBusiness{

@EJB
private SceValidadesRN sceValidadesRN;

private static final Log LOG = LogFactory.getLog(ValidadeMaterialON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceLoteFornecedorDAO sceLoteFornecedorDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	private static final long serialVersionUID = -4257023928561369311L;

	public enum ValidadeMaterialONExceptionCode implements BusinessExceptionCode {
		SCE_00697, SCE_00697_2, SCE_00701;
	}
	
	/**
	 * Persisti a validade no banco
	 * @param sceValidade
	 * @param movimento
	 * @param qtdeValidades
	 * @throws BaseException
	 */
	public void inserir(SceValidade sceValidade, SceMovimentoMaterial movimento, Integer qtdeValidades) throws BaseException{
		this.preInserir(sceValidade, movimento, qtdeValidades);
		this.getSceValidadesRN().inserir(sceValidade);
	}
	
	/**
	 * Persisti a validade no banco
	 * @param sceValidade
	 * @param movimento
	 * @param qtdeValidades
	 * @throws BaseException
	 */
	public void atualizar(SceValidade sceValidade, SceMovimentoMaterial movimento, Integer qtdeValidades, boolean somaNoFinal) throws BaseException{
		if(somaNoFinal){
			somaQuantidadeAntigaComNova(sceValidade, qtdeValidades);
			this.preAtualizar(sceValidade, movimento, qtdeValidades, false);
		}else{
			this.preAtualizar(sceValidade, movimento, qtdeValidades, true);
		}
		this.getSceValidadesRN().atualizar(sceValidade);
	}
	
	/**
	 * ORADB EVT_KEY_COMMIT, EVT_PRE_INSERT (INSERT)  
	 * Pré-inserir SceMovimentoMaterial
	 * @param sceMovimentoMaterial
	 * @throws BaseException
	 */
	protected void preInserir(SceValidade sceValidade, SceMovimentoMaterial movimento, Integer qtdeValidades) throws BaseException{
		validaQuantidades(movimento, qtdeValidades);
		validaTipoOperacaoBasica(sceValidade, movimento);
		defineEstoqueAlmoxarifado(sceValidade, movimento);
	}
	
	/**
	 * ORADB EVT_KEY_COMMIT (UPDATE)  
	 * Pré-UPDATE SceMovimentoMaterial
	 * @param sceMovimentoMaterial
	 * @throws BaseException
	 */
	protected void preAtualizar(SceValidade sceValidade, SceMovimentoMaterial movimento, Integer qtdeValidades, boolean validaQtdeLote) throws BaseException{
		validaQuantidades(movimento, qtdeValidades);
		if(validaQtdeLote){
			validaQuantidadeValidadeSomaLote(sceValidade, movimento);
		}
	}

	/**
	 * Valida as quantidades das validades e do movimento
	 * @param movimento
	 * @param qtdeValidades
	 * @throws BaseException
	 */
	public void validaQuantidades(SceMovimentoMaterial movimento, Integer qtdeValidades) throws BaseException {
		if(movimento != null && movimento.getQuantidade() != null && qtdeValidades > movimento.getQuantidade()){
			throw new ApplicationBusinessException(ValidadeMaterialONExceptionCode.SCE_00697);
		}
	}
	
	/**
	 * Método usado somente para a tela de bloqueio e desbloqueio com problema.
	 * @param sceValidade
	 * @param qtdeValidades
	 */
	private void somaQuantidadeAntigaComNova(SceValidade sceValidade, Integer qtdeValidades){
		sceValidade.setQtdeDisponivel(sceValidade.getQtdeDisponivel()+qtdeValidades);
	}
	
	/**
	 * Valida se a validade já possui algum lote e se a soma dos valores dos lotes não é maior que a quantidade da validade.
	 * @param sceValidade
	 * @param movimento
	 * @throws ApplicationBusinessException
	 */
	protected void validaQuantidadeValidadeSomaLote(SceValidade sceValidade, SceMovimentoMaterial movimento) throws ApplicationBusinessException{
		Integer counterLotes = 0;
		List<SceLoteFornecedor> lotes = getSceLoteFornecedorDAO().pesquisarLoteFornecedorPorMaterialValidade(movimento.getMaterial().getCodigo(), sceValidade.getId().getData());
		for (SceLoteFornecedor lote : lotes) {
			counterLotes += lote.getQuantidade();
		}

		if(sceValidade.getQtdeDisponivel() < counterLotes){
			throw new ApplicationBusinessException(ValidadeMaterialONExceptionCode.SCE_00697_2);
		}
	}
	
	/**
	 * Define o ealSeq da chave composta do objeto a persistir 
	 * @param sceValidade
	 * @param movimento
	 */
	public void defineEstoqueAlmoxarifado(SceValidade sceValidade, SceMovimentoMaterial movimento){
		if(movimento!=null){
			List<SceEstoqueAlmoxarifado> lstEal = getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueMaterialPorAlmoxarifado(movimento.getAlmoxarifado().getSeq(), movimento.getFornecedor().getNumero(), movimento.getMaterial().getCodigo());
			sceValidade.getId().setEalSeq(lstEal.get(0).getSeq());
		}else{
			sceValidade.getId().setEalSeq(sceValidade.getId().getEalSeq());
		}
	}
	
	/**
	 * Valida o tipo de operação básica
	 * @param movimento
	 * @throws BaseException
	 */
	public void validaTipoOperacaoBasica(SceValidade sceValidade, SceMovimentoMaterial movimento) throws BaseException {
		if(movimento != null && movimento.getTipoMovimento()!=null && !movimento.getTipoMovimento().getIndOperacaoBasica().equals(DominioIndOperacaoBasica.CR)){
			throw new ApplicationBusinessException(ValidadeMaterialONExceptionCode.SCE_00701);
		}else{
			sceValidade.setQtdeEntrada(sceValidade.getQtdeDisponivel());
			sceValidade.setQtdeConsumida(0);
		}
	}
	
	private SceValidadesRN getSceValidadesRN(){
		return sceValidadesRN;
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected SceLoteFornecedorDAO getSceLoteFornecedorDAO(){
		return sceLoteFornecedorDAO;
	}
}
