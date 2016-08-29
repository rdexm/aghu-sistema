package br.gov.mec.aghu.compras.parecer.business;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerMaterialDAO;
import br.gov.mec.aghu.compras.vo.PareceresVO;
import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoParecerMaterialON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoParecerMaterialON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoParecerMaterialDAO scoParecerMaterialDAO;
	
	@Inject
	private ScoMaterialDAO scoMaterialDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7518477434707318927L;
	
	public enum ScoParecerMaterialONExceptionCode implements
			BusinessExceptionCode {

		MENSAGEM_PARAM_OBRIG, MENSAGEM_ERRO_PARECER_MATERIAL_M01, MENSAGEM_ERRO_PARECER_MATERIAL_M02, CAMPO_OBRIGATORIO, MENSAGEM_OPERACAO_MATERIAL_INATIVO;
	}

	public List<PareceresVO> pesquisarPareceres(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro,
			Boolean matIndEstocavel, Integer firstResult, Integer maxResult) {

         List<PareceresVO> listaPareceres = new ArrayList<PareceresVO>();
		
         List<PareceresVO> listaParAval = getScoParecerMaterialDAO().pesquisarPareceresAvaliacoes(
 				material, grupoMaterial, marcaComercial, modeloComercial,
 				apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, nroRegistro, matIndEstocavel);

         if (listaParAval != null) {
        	 for (PareceresVO par : listaParAval) {
        		 par.setOcorrencia(false);
        	 }
        	 listaPareceres.addAll(listaParAval);
         }
		
         List<PareceresVO> listaParOcorr = getScoParecerMaterialDAO().pesquisarPareceresOcorrencias(
 				material, grupoMaterial, marcaComercial, modeloComercial,
 				apenasUltimosPareceres, situacao, parecerFinal, pasta,
 				nroSubPasta, nroRegistro, matIndEstocavel);

         if (listaParOcorr != null) {
        	 for (PareceresVO par : listaParOcorr) {
        		 par.setOcorrencia(true);
        	 }
        	 listaPareceres.addAll(listaParOcorr);
         }
		
     	if (!listaPareceres.isEmpty()) {
			int indPrimeiro = firstResult;
			int indUltimo = firstResult + maxResult;
			int tamLista = listaPareceres.size();
			if (indUltimo > tamLista) {
				indUltimo = tamLista;
			}
			return listaPareceres.subList(indPrimeiro, indUltimo);
		} 
		return listaPareceres;
	}
	
	public Long pesquisarPareceresCount(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro, Boolean matIndEstocavel) {
		Long count = 0L;
		
		Long retornoAF = this.getScoParecerMaterialDAO().pesquisarPareceresAvaliacoesCount(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, nroRegistro, matIndEstocavel);
		Long retornoAFP = this.getScoParecerMaterialDAO().pesquisarPareceresOcorrenciasCount(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, nroRegistro, matIndEstocavel);
		count = retornoAF + retornoAFP;
		
		return count;
	}
	

	public void validarParecerMaterial(ScoParecerMaterial scoParecerMaterial) throws ApplicationBusinessException{
		ScoParecerMaterial searchParecerMaterial = this.getScoParecerMaterialDAO().obterParecerTecnicoDuplicidade(scoParecerMaterial);
				
		if (searchParecerMaterial != null) {
			
			/*if (searchParecerMaterial.getScoMarcaModelo() == null){
				throw new ApplicationBusinessException(ScoParecerMaterialONExceptionCode.MENSAGEM_ERRO_PARECER_MATERIAL_M01);
			}*/
			throw new ApplicationBusinessException(ScoParecerMaterialONExceptionCode.MENSAGEM_ERRO_PARECER_MATERIAL_M02);	
		}
	}
	
	public void atualizarParecerMaterialAnteriores(ScoParecerMaterial scoParecerMaterial){
		
				
			this.atualizarParecerMaterialAnterioresPasta(scoParecerMaterial);			
		    this.atualizarParecerMaterialAnterioresSubPasta(scoParecerMaterial); 
		
		
	}	
	
	public void atualizarParecerMaterialAnterioresPasta(ScoParecerMaterial scoParecerMaterial){
		
		if (scoParecerMaterial.getOrigemParecerTecnico() != null){
			
			List<ScoParecerMaterial> listaPareceresMaterial = this.getScoParecerMaterialDAO().pesquisarParecerTecnicoMaterialMarca(scoParecerMaterial.getMaterial(), null);
			
			for(ScoParecerMaterial itemScoParecerMaterial:listaPareceresMaterial){
				itemScoParecerMaterial.setOrigemParecerTecnico(scoParecerMaterial.getOrigemParecerTecnico());						
				this.getScoParecerMaterialDAO().atualizar(itemScoParecerMaterial);
						
			}
			
		}
	}
	
    public void atualizarParecerMaterialAnterioresSubPasta(ScoParecerMaterial scoParecerMaterial){
		
		if (scoParecerMaterial.getNumeroSubPasta() != null){
			
			List<ScoParecerMaterial> listaPareceresMaterial = this.getScoParecerMaterialDAO().pesquisarParecerTecnicoMaterialMarca(scoParecerMaterial.getMaterial(), scoParecerMaterial.getMarcaComercial());
			
			for(ScoParecerMaterial itemScoParecerMaterial:listaPareceresMaterial){
				itemScoParecerMaterial.setNumeroSubPasta(scoParecerMaterial.getNumeroSubPasta());					
				this.getScoParecerMaterialDAO().atualizar(itemScoParecerMaterial);						
			}
			
		}
	}
	
	
	
	public void alterar(ScoParecerMaterial scoParecerMaterial)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (scoParecerMaterial == null) {
			throw new ApplicationBusinessException(
					ScoParecerMaterialONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		validarParecerMaterial(scoParecerMaterial);	
		
		atualizarParecerMaterialAnteriores(scoParecerMaterial);
		
		scoParecerMaterial.setDtAlteracao(new Date());
		scoParecerMaterial.setServidorAlteracao(servidorLogado);
		
		this.getScoParecerMaterialDAO().merge(scoParecerMaterial);		
	}

	public void inserir(ScoParecerMaterial scoParecerMaterial)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (scoParecerMaterial == null) {
			throw new ApplicationBusinessException(
					ScoParecerMaterialONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		ScoMaterial scoMaterial = scoMaterialDAO.obterMaterialPorId(scoParecerMaterial.getMaterial().getCodigo());
		
		if (DominioSituacao.I.equals(scoMaterial.getIndSituacao())){
			throw new ApplicationBusinessException(
					ScoParecerMaterialONExceptionCode.MENSAGEM_OPERACAO_MATERIAL_INATIVO);
		}

		validarParecerMaterial(scoParecerMaterial);
		atualizarParecerMaterialAnteriores(scoParecerMaterial);
		
		scoParecerMaterial.setDtCriacao(new Date());
		scoParecerMaterial.setDtAlteracao(new Date());
		scoParecerMaterial.setServidorCriacao(servidorLogado);
		scoParecerMaterial.setServidorAlteracao(servidorLogado);
		
		this.getScoParecerMaterialDAO().persistir(scoParecerMaterial);			
		
	}

	
	protected ScoParecerMaterialDAO getScoParecerMaterialDAO() {
		return scoParecerMaterialDAO;
	}

	/**
	 * Realiza a verificação dos campos obrigatórios para o cadastro de Parecer.
	 * 
	 * @param parecerMaterial
	 * @throws CompositeExceptionSemRollback
	 */
	public void verificarCampoObrigatorioCadastroParecer(final ScoParecerMaterial parecerMaterial) throws ApplicationBusinessException {
		if (parecerMaterial != null) {
			final List<ApplicationBusinessException> exs = new ArrayList<ApplicationBusinessException>(2);
			if (parecerMaterial.getMaterial() == null) {
				exs.add(new ApplicationBusinessException(ScoParecerMaterialONExceptionCode.CAMPO_OBRIGATORIO, this.getResourceBundleValue(
						"LABEL_CADASTRO_PARECER_MATERIAL")));
			}
			if (parecerMaterial.getMarcaComercial() == null) {
				exs.add(new ApplicationBusinessException(ScoParecerMaterialONExceptionCode.CAMPO_OBRIGATORIO, this.getResourceBundleValue(
						"LABEL_CADASTRO_PARECER_MARCA")));
			}
			if (!exs.isEmpty()) {
				final ApplicationBusinessException ex = new ApplicationBusinessException(exs.get(0));
				throw ex;
			}
		}
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}