package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class HistoricoProblemaMaterialON extends BaseBusiness {

@EJB
private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
@EJB
private SceHistoricoProblemaMaterialRN sceHistoricoProblemaMaterialRN;

private static final Log LOG = LogFactory.getLog(HistoricoProblemaMaterialON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceValidadeDAO sceValidadeDAO;

@Inject
private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2382982587270875605L;

	public enum HistoricoProblemaMaterialONExceptionCode implements BusinessExceptionCode {
		SCE_00706, SCE_00705, SCE_00723, SCE_00630, SCE_00698;
	}

	/**
	 * Metodo que insere/atualiza os históricos de problemas de um material com problema
	 * @param historico
	 * @param tipoMovimento
	 * @param qtdeAcaoBloqueioDesbloqueio
	 * @throws BaseException
	 */
	public void bloqueioDesbloqueioQuantidadesProblema(SceHistoricoProblemaMaterial historico, SceTipoMovimento tipoMovimento, Integer qtdeAcaoBloqueioDesbloqueio, String nomeMicrocomputador) throws BaseException{


		SceEstoqueAlmoxarifado estoqueAlmoxarifadoAux = getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(historico.getSceEstqAlmox().getAlmoxarifado().getSeq(), historico.getSceEstqAlmox().getMaterial().getCodigo(), historico.getSceEstqAlmox().getFornecedor().getNumero());
		if(estoqueAlmoxarifadoAux==null){
			throw new ApplicationBusinessException(HistoricoProblemaMaterialONExceptionCode.SCE_00630);
		}

		historico.setSceEstqAlmox(estoqueAlmoxarifadoAux);

		if(tipoMovimento.getIndQtdeBloqueada().equals(DominioIndOperacaoBasica.DB)){
			retiraBloqueadoMatEstoque(historico, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
		}

		if(tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.DB)){
			retiraDisponivelMatEstoque(historico, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
		}

		if(tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.CR)){
			acumulaDisponivelMatEstoque(historico, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
		}

		if(tipoMovimento.getIndQtdeBloqueada().equals(DominioIndOperacaoBasica.CR)){
			acumulaBloqueadoMatEstoque(historico, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
		}

		if(tipoMovimento.getIndQtdeBloqProblema().equals(DominioIndOperacaoBasica.CR)){
			bloqueiaMatProblema(historico, qtdeAcaoBloqueioDesbloqueio);
		}

		if(tipoMovimento.getIndQtdeBloqProblema().equals(DominioIndOperacaoBasica.DB)){
			desbloqueiaMatProblema(historico, qtdeAcaoBloqueioDesbloqueio);
		}

		historico.setIndEfetivado(false);
		if(historico.getSeq() == null){
			getSceHistoricoProblemaMaterialRN().inserir(historico, true);
		}else{
			getSceHistoricoProblemaMaterialRN().atualizar(historico, false);
		}
		this.getSceHistoricoProblemaMaterialDAO().flush();
	}

	/** Verifica ao clicar no botão gravar a obrigatoriedade de inserir validades.
	 * ORADB VERIFICA_EXIGE_VALID_ESTQ
	 * @param historico
	 * @param tipoMovimento
	 * @throws BaseException
	 */
	public void verificaBotaoVoltarBloqueioDesbloqueioProblema(SceHistoricoProblemaMaterial historico, SceTipoMovimento tipoMovimento,List<SceValidade> listaValidadeInicial, Boolean mostrouGradeValidade) throws BaseException{
		if(historico != null && historico.getSceEstqAlmox() != null && historico.getSceEstqAlmox().getAlmoxarifado() != null 
				&& historico.getSceEstqAlmox().getMaterial() != null && historico.getSceEstqAlmox().getFornecedor() != null 
				&& mostrouGradeValidade){
			SceEstoqueAlmoxarifado estoqueAlmoxarifadoAux = getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(historico.getSceEstqAlmox().getAlmoxarifado().getSeq(), historico.getSceEstqAlmox().getMaterial().getCodigo(), historico.getSceEstqAlmox().getFornecedor().getNumero());

			if (estoqueAlmoxarifadoAux != null) {

				List<SceValidade> validades = getSceValidadeDAO().pesquisarValidadeEalSeqDataValidadeComQuantidadeDisponivel(estoqueAlmoxarifadoAux.getSeq(), null);

				Boolean validadeAlterada =  validaAlteracaoValidade(listaValidadeInicial, validades);//Necessário alterar a validade para o desbloqueio.

				if (tipoMovimento!=null && estoqueAlmoxarifadoAux != null && validades != null &&
						(tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.CR)
								|| tipoMovimento.getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.CR)
								|| tipoMovimento.getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.CR))
								&& estoqueAlmoxarifadoAux.getIndControleValidade() && validades!=null && (!validadeAlterada || validades.size()==0)){
					throw new ApplicationBusinessException(HistoricoProblemaMaterialONExceptionCode.SCE_00698);
				}

			}

		}

	}

	private Boolean validaAlteracaoValidade(
			List<SceValidade> listaValidadeInicial, List<SceValidade> validades) {

		Boolean retorno = Boolean.FALSE;
		int aux = 0;

		if(validades!= null && !validades.isEmpty()){

			if(validades.size() == listaValidadeInicial.size()){
				for(SceValidade valid: validades){
					if(!valid.equals(listaValidadeInicial.get(aux))){
						retorno = Boolean.TRUE;
					}
					aux++;
				}
			}else{
				retorno = Boolean.TRUE;
			}
			
		}

		return retorno;
	}



	private void retiraBloqueadoMatEstoque(SceHistoricoProblemaMaterial historico, Integer qtdeAcaoBloqueioDesbloqueio, String nomeMicrocomputador) throws BaseException{

		if(qtdeAcaoBloqueioDesbloqueio > historico.getSceEstqAlmox().getQtdeBloqueada()){
			throw new ApplicationBusinessException(HistoricoProblemaMaterialONExceptionCode.SCE_00706);
		}

		SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = historico.getSceEstqAlmox();
		sceEstoqueAlmoxarifado.setQtdeBloqueada(historico.getSceEstqAlmox().getQtdeBloqueada() - qtdeAcaoBloqueioDesbloqueio);
		getSceEstoqueAlmoxarifadoRN().atualizar(sceEstoqueAlmoxarifado, nomeMicrocomputador, true);
	}

	private void retiraDisponivelMatEstoque(SceHistoricoProblemaMaterial historico, Integer qtdeAcaoBloqueioDesbloqueio, String nomeMicrocomputador) throws BaseException{
		if(qtdeAcaoBloqueioDesbloqueio > historico.getSceEstqAlmox().getQtdeDisponivel()){
			throw new ApplicationBusinessException(HistoricoProblemaMaterialONExceptionCode.SCE_00705);
		}

		SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = historico.getSceEstqAlmox();
		sceEstoqueAlmoxarifado.setQtdeDisponivel(historico.getSceEstqAlmox().getQtdeDisponivel() - qtdeAcaoBloqueioDesbloqueio);
		getSceEstoqueAlmoxarifadoRN().atualizar(sceEstoqueAlmoxarifado, nomeMicrocomputador, true);
	}

	private void acumulaDisponivelMatEstoque(SceHistoricoProblemaMaterial historico, Integer qtdeAcaoBloqueioDesbloqueio, String nomeMicrocomputador) throws BaseException{
		if(historico.getQtdeProblema() < qtdeAcaoBloqueioDesbloqueio){
			throw new ApplicationBusinessException(HistoricoProblemaMaterialONExceptionCode.SCE_00723);
		}

		getSceHistoricoProblemaMaterialRN().validaQtdeProblemaMenorDesbloqueadaMaisDevolvidaAux(historico, qtdeAcaoBloqueioDesbloqueio);

		historico.setQtdeDesbloqueada(historico.getQtdeDesbloqueada() + qtdeAcaoBloqueioDesbloqueio);

		SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = historico.getSceEstqAlmox();
		sceEstoqueAlmoxarifado.setQtdeDisponivel(historico.getSceEstqAlmox().getQtdeDisponivel() + qtdeAcaoBloqueioDesbloqueio);
		getSceEstoqueAlmoxarifadoRN().atualizar(sceEstoqueAlmoxarifado, nomeMicrocomputador, true);
	}

	private void acumulaBloqueadoMatEstoque(SceHistoricoProblemaMaterial historico, Integer qtdeAcaoBloqueioDesbloqueio, String nomeMicrocomputador) throws BaseException{
		if(historico.getQtdeProblema() < qtdeAcaoBloqueioDesbloqueio){
			throw new ApplicationBusinessException(HistoricoProblemaMaterialONExceptionCode.SCE_00723);
		}

		getSceHistoricoProblemaMaterialRN().validaQtdeProblemaMenorDesbloqueadaMaisDevolvidaAux(historico, qtdeAcaoBloqueioDesbloqueio);

		historico.setQtdeDesbloqueada(historico.getQtdeDesbloqueada() + qtdeAcaoBloqueioDesbloqueio);

		SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = historico.getSceEstqAlmox();
		sceEstoqueAlmoxarifado.setQtdeBloqueada(historico.getSceEstqAlmox().getQtdeBloqueada() + qtdeAcaoBloqueioDesbloqueio);
		getSceEstoqueAlmoxarifadoRN().atualizar(sceEstoqueAlmoxarifado, nomeMicrocomputador, true);
	}

	private void bloqueiaMatProblema(SceHistoricoProblemaMaterial historico, Integer qtdeAcaoBloqueioDesbloqueio){
		historico.setQtdeProblema(historico.getQtdeProblema() + qtdeAcaoBloqueioDesbloqueio);
	}

	private void desbloqueiaMatProblema(SceHistoricoProblemaMaterial historico, Integer qtdeAcaoBloqueioDesbloqueio) throws BaseException{
		if(qtdeAcaoBloqueioDesbloqueio > historico.getQtdeProblema()){
			throw new ApplicationBusinessException(HistoricoProblemaMaterialONExceptionCode.SCE_00706);
		}

		historico.setQtdeProblema(historico.getQtdeProblema()-qtdeAcaoBloqueioDesbloqueio);
	}

	private SceHistoricoProblemaMaterialRN getSceHistoricoProblemaMaterialRN(){
		return sceHistoricoProblemaMaterialRN;
	}


	private SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO(){
		return sceHistoricoProblemaMaterialDAO;
	}

	private SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN(){
		return sceEstoqueAlmoxarifadoRN;
	}

	private SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}

	private SceValidadeDAO getSceValidadeDAO(){
		return sceValidadeDAO;
	}

}
