package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnidsId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * AghUnidadesFuncionais.
 * 
 * @author lalegre & csvirgens
 * 
 */
@Stateless
public class ImpressoraPadraoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ImpressoraPadraoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6634708173054846108L;

	private enum ImpressoraPadraoONExceptionCode implements
			BusinessExceptionCode {
		AIP_00403, 
		AIP_00416,
		AAC_00240, 
		IMPRESSAO_CANCELADA,
		ERRO_REMOVER_IMPRESSORA, 
		ERRO_PERSISTIR_IMPRESSORA, 
		ERRO_ATUALIZAR_IMPRESSORA,
		TIPO_IMPRESSAO_OBRIGATORIO;
	}

	/**
	 * Metódo para verifica se a unidade já não possui mais que uma impressora
	 * por tipo
	 * 
	 * @param unfSeq
	 * @return Count
	 */
	public Long pesquisaImpressorasCount(Short unfSeq) {
		return this.getAghuFacade().pesquisaImpressorasCount(unfSeq);
	}

	/**
	 * Metódo que obtem o nome da impressora cadastrada para a unidade funcional
	 * especificada e parametro da unidade.
	 * 
	 * @param aghParam
	 * @param tipoImpressora
	 * @return nomeImpressora
	 * @throws ApplicationBusinessException
	 */
	public ImpImpressora obterImpressora(AghParametros aghParam,
			TipoDocumentoImpressao tipoImpressora) throws ApplicationBusinessException {
		ImpImpressora impImpressora;

		if (aghParam != null) {
			impImpressora = this.obterImpressora(aghParam.getVlrNumerico()
					.shortValue(), tipoImpressora);
		} else {
			throw new ApplicationBusinessException(
					ImpressoraPadraoONExceptionCode.AIP_00416);
		}

		return impImpressora;
	}
	
	/**
	 * Metódo que obtem a <code>ImpImpressora</code> cadastrada para a unidade
	 * funcional.
	 * 
	 * ORADB CURSOR C_PRINT (C_UNF_SEQ NUMBER)
	 * 
	 * @param unfSeq
	 * @param tipoImpressora
	 * @return nome da impressora
	 * @throws ApplicationBusinessException
	 */
	public ImpImpressora obterImpressora(Short unfSeq,
			TipoDocumentoImpressao tipoImpressora) throws ApplicationBusinessException {
		List<ImpImpressora> listImpImpressora = this.getAghuFacade().listarImpImpressorasPorUnfSeqETipoDocumentoImpressao(unfSeq, tipoImpressora);
		
		ImpImpressora res = null;
		if(listImpImpressora != null && !listImpImpressora.isEmpty()){
			for (ImpImpressora impressora:listImpImpressora){
				res = impressora;
				if(res != null){
					break;
				}
			}
		}

		if (res== null) {
			throw new ApplicationBusinessException(
					ImpressoraPadraoONExceptionCode.AIP_00403, tipoImpressora.toString(), unfSeq);
		}

		return res;
	}

	/**
	 * Cancelar a impressão
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void cancelarImpressao() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(
				ImpressoraPadraoONExceptionCode.IMPRESSAO_CANCELADA);
	}

	/**
	 * Método responsável pela persistência de uma Impressora.
	 * @param impressora
	 * @throws ApplicationBusinessException
	 */	
	public void incluirImpressora(List<AghImpressoraPadraoUnids> listaImpressoras, List<AghImpressoraPadraoUnids> listaImpressorasOld,
			AghUnidadesFuncionais unidade) throws ApplicationBusinessException {
		try {
			IAghuFacade aghuFacade = this.getAghuFacade();
			
			//Removendo as diarias excluídas
			
			List<AghImpressoraPadraoUnids> listaImps = this.aghuFacade.obterAghImpressoraPadraoUnids(unidade.getSeq());

			if(listaImps != null && !listaImps.isEmpty()){
				for (AghImpressoraPadraoUnids impressoraOld: listaImps){
					AghImpressoraPadraoUnids item = aghuFacade.obterAghImpressoraPadraoUnidsPorChavePrimaria(impressoraOld.getId());
					aghuFacade.removerAghImpressoraPadraoUnids(item, false);
				}
				flush();
			}
			
			Short contador = 1;
			for(AghImpressoraPadraoUnids imp: listaImpressoras ){				
				// setando o SEQUENCE				
				AghImpressoraPadraoUnidsId id = new AghImpressoraPadraoUnidsId();
				id.setUnfSeq(unidade.getSeq());			
				id.setSeqp(contador++);
				imp.setId(id);
				
				aghuFacade.inserirAghImpressoraPadraoUnids(imp, false);				
			}
			flush();
			
		} catch (Exception e) {
			logError("Erro ao incluir a impressora.", e);
			throw new ApplicationBusinessException(
					ImpressoraPadraoONExceptionCode.ERRO_PERSISTIR_IMPRESSORA);
		}
	}
	
	/*
	 * Retorna as impressoras da unidade funcional.
	 * 
	 * @param seq da unidade funcional.
	 * 
	 * @return Lista de impressoras
	 */
	public List<AghImpressoraPadraoUnids> obterAghImpressoraPadraoUnids(Short seq) {
		return this.getAghuFacade().obterAghImpressoraPadraoUnids(seq);
	}

	public void validaCamposImpressoraPadrao(TipoDocumentoImpressao tipoImpressao) throws ApplicationBusinessException {
		if (tipoImpressao == null) {
			throw new ApplicationBusinessException(ImpressoraPadraoONExceptionCode.TIPO_IMPRESSAO_OBRIGATORIO);
		}
	}
		
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	public Boolean verificaExisteImpressoraPadrao(Short unfSeq,
			TipoDocumentoImpressao tipoImpressora) {
		List<ImpImpressora> listImpImpressora = this.getAghuFacade().listarImpImpressorasPorUnfSeqETipoDocumentoImpressao(unfSeq, tipoImpressora);

		return listImpImpressora != null && !listImpImpressora.isEmpty();
	}
	
}
