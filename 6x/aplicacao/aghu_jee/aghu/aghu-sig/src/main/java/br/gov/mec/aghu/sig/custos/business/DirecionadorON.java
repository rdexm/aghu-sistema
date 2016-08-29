package br.gov.mec.aghu.sig.custos.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.dao.SigDirecionadoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DirecionadorON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(DirecionadorON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigDirecionadoresDAO sigDirecionadoresDAO;

	private static final long serialVersionUID = -7974290715629180089L;

	public enum DirecionadorONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DIRECIONADOR_NOME_EXISTENTE,
		MENSAGEM_CAMPOS_OBRIGATORIOS_DIRECIONADOR_TIPO_ATIVIDADE,
		MENSAGEM_CAMPOS_OBRIGATORIOS_DIRECIONADOR_TIPO_RECURSO,
		MENSAGEM_CAMPOS_OBRIGATORIOS_DIRECIONADOR_TIPO_CLIENTE
	}
	
	public void persistirDirecionador(SigDirecionadores direcionador,  RapServidores servidorResponsavel) throws ApplicationBusinessException {
		
		this.validarInformacoesDirecionador(direcionador);
		
		direcionador.setServidorResp(servidorResponsavel);
		
		if (direcionador.getSeq() == null) {
			direcionador.setCriadoEm(new Date());
			this.getSigDirecionadoresDAO().persistir(direcionador);
		} else {
			this.getSigDirecionadoresDAO().merge(direcionador);
		}
	}
	
	public List<DominioTipoCalculoObjeto> listarTiposCalculoObjeto(DominioTipoDirecionadorCustos tipoDirecionadorCusto){
		List<DominioTipoCalculoObjeto> lista = new ArrayList<DominioTipoCalculoObjeto>();
		if(tipoDirecionadorCusto != null){
		
			if(tipoDirecionadorCusto == DominioTipoDirecionadorCustos.AT){
				lista.add(DominioTipoCalculoObjeto.DP);
				lista.add(DominioTipoCalculoObjeto.PR);
				lista.add(DominioTipoCalculoObjeto.PP);
			}
			else if(tipoDirecionadorCusto == DominioTipoDirecionadorCustos.RT){
				lista.add(DominioTipoCalculoObjeto.PE);
				lista.add(DominioTipoCalculoObjeto.PM);
			}
		}
		return lista;
	}
	
	private void validarInformacoesDirecionador(SigDirecionadores direcionador) throws ApplicationBusinessException{
		
		if (!this.validarNomeUnicoDirecionador(direcionador)) {
			throw new ApplicationBusinessException(DirecionadorONExceptionCode.MENSAGEM_DIRECIONADOR_NOME_EXISTENTE);
		}

		if (direcionador.getIndTipo() == DominioTipoDirecionadorCustos.AT) {
			if (this.validarCamposObrigatoriosParaTipoAtividade(direcionador)) {
				throw new ApplicationBusinessException(DirecionadorONExceptionCode.MENSAGEM_CAMPOS_OBRIGATORIOS_DIRECIONADOR_TIPO_ATIVIDADE);
			} else {
				direcionador.setIndTempo(null);
				direcionador.setFatConvHoras(null);
			}
		} else if(direcionador.getIndTipo() == DominioTipoDirecionadorCustos.RC){
			if (this.validarCamposObrigatoriosParaTipoRecurso(direcionador)) {
				throw new ApplicationBusinessException(DirecionadorONExceptionCode.MENSAGEM_CAMPOS_OBRIGATORIOS_DIRECIONADOR_TIPO_RECURSO);
			} else {
				direcionador.setIndTipoCalculo(null);
				direcionador.setIndNroExecucoes(false);
				direcionador.setOperacao(null);
			}
		} else if(direcionador.getIndTipo() == DominioTipoDirecionadorCustos.RT){
			if (this.validarCamposObrigatoriosParaTipoCliente(direcionador)) {
				throw new ApplicationBusinessException(DirecionadorONExceptionCode.MENSAGEM_CAMPOS_OBRIGATORIOS_DIRECIONADOR_TIPO_CLIENTE);
			} else {
				direcionador.setIndNroExecucoes(false);
				direcionador.setOperacao(null);
				
				if(direcionador.getIndTipoCalculo() != DominioTipoCalculoObjeto.PE){
					direcionador.setIndColetaSistema(false);
				}
			}
		} else if(direcionador.getIndTipo() == DominioTipoDirecionadorCustos.RO){
				direcionador.setIndNroExecucoes(false);
				direcionador.setOperacao(null);
				direcionador.setIndTipoCalculo(null);
				direcionador.setFatConvHoras(null);
				direcionador.setIndTempo(null);
		}
	}
	
	private boolean validarCamposObrigatoriosParaTipoCliente(SigDirecionadores direcionador) {
		return (direcionador.getIndTipoCalculo() == null);
	}

	private boolean validarCamposObrigatoriosParaTipoAtividade(SigDirecionadores direcionador) {
		return (direcionador.getOperacao() == null || direcionador.getIndTipoCalculo() == null || direcionador.getIndNroExecucoes() == null);
	}

	private boolean validarCamposObrigatoriosParaTipoRecurso(SigDirecionadores direcionador) {
		return (direcionador.getIndTempo() == null);
	}
	
	private boolean validarNomeUnicoDirecionador(SigDirecionadores direcionador){
		boolean retorno = true;
		List<SigDirecionadores> listaDirecionador = this.getSigDirecionadoresDAO().listarTodos();
		
		if (listaDirecionador == null || listaDirecionador.isEmpty() || listaDirecionador.size() == 0) {
			return retorno;
		}
		for (SigDirecionadores sigDirecionador : listaDirecionador){
			if (direcionador.getSeq() == null && direcionador.getNome().equalsIgnoreCase(sigDirecionador.getNome())) {
				retorno = false;
				break;
			}
			if (direcionador.getSeq() != null && direcionador.getSeq().intValue() != sigDirecionador.getSeq().intValue()
					&& direcionador.getNome().equalsIgnoreCase(sigDirecionador.getNome())) {
				retorno = false;
				break;
			}
		}
		return retorno;
	}
	
	public List<SigDirecionadores> pesquisaDirecionadoresDoObjetoCusto(
			SigObjetoCustoVersoes versao,
			DominioSituacao direcionadorRateioSituacao,
			DominioTipoDirecionadorCustos indTipo,
			DominioTipoCalculoObjeto indTipoCalculo) {
		List<SigDirecionadores> direcionadores = this.getSigDirecionadoresDAO().pesquisaDirecionadoresDoObjetoCusto(versao, direcionadorRateioSituacao, indTipo, indTipoCalculo);
		for (SigDirecionadores direcionador : direcionadores) {
			direcionador.getNome();//Inicializa os atributos de cada direcionador
		}
		return direcionadores;
	}

	protected SigDirecionadoresDAO getSigDirecionadoresDAO() {
		return sigDirecionadoresDAO;
	}
}
