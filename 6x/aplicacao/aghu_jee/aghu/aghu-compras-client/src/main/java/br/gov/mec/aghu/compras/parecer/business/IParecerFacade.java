package br.gov.mec.aghu.compras.parecer.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.compras.vo.PareceresAvaliacaoVO;
import br.gov.mec.aghu.compras.vo.PareceresVO;
import br.gov.mec.aghu.compras.vo.PropFornecAvalParecerVO;
import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoParecer;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.model.ScoParecerAvalConsul;
import br.gov.mec.aghu.model.ScoParecerAvalDesemp;
import br.gov.mec.aghu.model.ScoParecerAvalTecnica;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public interface IParecerFacade extends Serializable{
	
	public List<PareceresVO> pesquisarPareceres(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro,
			Boolean matIndEstocavel, Integer firstResult, Integer maxResult);
	
	public ScoParecerMaterial obterParecer(Integer codigo);	
	
	public ScoParecerMaterial obterParecerTecnicoDuplicidade(ScoParecerMaterial scoParecerMaterial);
	
	public void alterarParecerMaterial(ScoParecerMaterial scoParecerMaterial)
			throws ApplicationBusinessException;
		
	public ScoParecerMaterial obterParecerTecnicoAtivo(ScoParecerMaterial scoParecerMaterial, Boolean isMarca);
	
	public void inserirParecerMaterial(ScoParecerMaterial scoParecerMaterial)
			throws ApplicationBusinessException;
	
	public ScoParecerAvaliacao obterUltimaAvaliacaoParecer(ScoParecerMaterial scoParecerMaterial);
	
	public ScoParecerOcorrencia obterUltimaOcorrenciaParecer(ScoParecerMaterial scoParecerMaterial);	
		
	public void persistirParecerAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao, ScoParecerAvalTecnica scoParecerAvalTecnica, 
            ScoParecerAvalConsul scoParecerAvalConsul, ScoParecerAvalDesemp scoParecerAvalDesemp) throws ApplicationBusinessException;
	
	public ScoParecerAvaliacao obterParecerAvaliacaoPorCodigo(Integer codigo);	
	
	public ScoParecerAvalTecnica obterParecerAvalTecnicaPorAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao);
		
	public ScoParecerAvalConsul obterParecerAvalConsulPorAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao);
	
	public ScoParecerAvalDesemp obterParecerAvalDesempPorAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao);	
	
	public  List<ScoParecerOcorrencia> listaOcorrenciaParecer(ScoParecerMaterial scoParecerMaterial, DominioSituacao situacao);
	
	public void persistirParecerOcorrencia(ScoParecerOcorrencia scoParecerOcorrencia) throws ApplicationBusinessException;
		
	public ScoParecerOcorrencia clonarParecerOcorrencia(ScoParecerOcorrencia  scoParecerOcorrencia) throws ApplicationBusinessException;
	
	public List<PareceresAvaliacaoVO> listaAvaliacaoParecer(ScoParecerMaterial scoParecerMaterial);
	
	
	public List<PropFornecAvalParecerVO> pesquisarItensPropostaFornecedorPAC(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer numeroPAC, List<DominioSituacaoParecer> listaSituacaoParecer);
	
	
	public Long contarItensPropostaFornecedorPAC(Integer numeroPAC, List<DominioSituacaoParecer> listaSituacaoParecer);
	
	
	/**
	 * Obtem SC para análise técnica a partir do item da proposta do fornecedor.
	 * 
	 * @param id ID do Item
	 * @return ID da SC
	 */
	public Integer obterScParaAnaliseTecnica(ScoItemPropostaFornecedorId id);
	
	/**
	 * Grava análise técnica de item de proposta de fornecedor.
	 * 
	 * @param itemProposta Item de Proposta
	 * @throws BaseException
	 */
	public void gravarAnaliseTecnica(ScoItemPropostaFornecedor itemProposta)
		throws BaseException;

	/**
	 * Realiza a verificação dos campos obrigatórios.
	 * 
	 * @param parecerMaterial
	 * @throws CompositeExceptionSemRollback
	 * @throws ApplicationBusinessException 
	 */
	public void verificarCampoObrigatorio(ScoParecerMaterial parecerMaterial) throws ApplicationBusinessException;
	
	public Integer obterMaxNumeroSubPasta(ScoOrigemParecerTecnico pasta);
	
	public Long pesquisarPareceresCount(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro,
			Boolean matIndEstocavel);
	
}
