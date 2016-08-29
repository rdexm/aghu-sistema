package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.dominio.DominioWorkflowExamePatologia;
import br.gov.mec.aghu.exames.dao.AelConfigExLaudoUnicoDAO;
import br.gov.mec.aghu.exames.dao.AelSecaoConfExamesDAO;
import br.gov.mec.aghu.exames.vo.SecaoConfExameVO;
import br.gov.mec.aghu.model.AelSecaoConfExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelSecaoConfExamesRN extends BaseBusiness {

	
private static final Log LOG = LogFactory.getLog(AelSecaoConfExamesRN.class);

	@Override
	@Deprecated
	public Log getLogger() {
	return LOG;
	}
	
	@Inject
	private AelSecaoConfExamesDAO aelSecaoConfExamesDAO;
	
	@Inject
	private AelConfigExLaudoUnicoDAO aelConfigExLaudoUnicoDAO;
	
	private static final long serialVersionUID = -4819028681533768840L;
	
	public enum AelSecaoConfExameRNExceptionCode implements	BusinessExceptionCode {
		NENHUMA_SECAO_INFORMADA_MANTER_CARACTERISTICA_MATERIAL_ANALISE, 
		NENHUM_REGISTRO_ALTERADO_MANTER_CARACTERISTICA_MATERIAL_ANALISE
	}
	
	/**
	 * Método utilizado para salvar uma nova configurção de Exames. 
	 * @param lista
	 * @param lu2Seq
	 * @param servidor
	 * @throws AGHUNegocioExceptionSemRollback
	 */
	public void salvarSecaoConfExames(List<SecaoConfExameVO> lista, Integer lu2Seq, RapServidores servidor) throws ApplicationBusinessException{
		
		if(this.validarAlgumMarcado(lista)){		
			Boolean algumFoiAlterado = Boolean.FALSE;
			for(SecaoConfExameVO vo : lista){
				Boolean foiAlterado = Boolean.FALSE;
				Boolean foiAlteradoIndObrigatorio = Boolean.FALSE;
				Boolean foiAlteradoIndImpressao = Boolean.FALSE;
				if(vo.getSecaoConfExames() != null){
					this.getAelSecaoConfExamesDAO().desatachar(vo.getSecaoConfExames());
					
					//verifica se foi alterado de ativo para inativo ou vice-versa
					foiAlterado = this.validarAlteracaoNaSituacao(vo);
					foiAlteradoIndObrigatorio = this.validarAlteracaoIndObrigatorio(vo);
					foiAlteradoIndImpressao = this.validarAlteracaoIndImpressao(vo);
					
					if(foiAlterado){
						algumFoiAlterado = Boolean.TRUE;
						if(vo.getAtivo()){ //Se foi flagado True, entao ativa
							vo.getSecaoConfExames().setIndSituacao(DominioSituacao.A);
						} else { // Se foi flagado False, entao desativa
							vo.getSecaoConfExames().setIndSituacao(DominioSituacao.I);
							vo.setSecaoObrigatoria(null);		
						}
					}
					if(foiAlteradoIndObrigatorio){
						algumFoiAlterado = Boolean.TRUE;
						vo.getSecaoConfExames().setIndObrigatorio(vo.getObrigatorio());
					}
					if(foiAlteradoIndImpressao){
						algumFoiAlterado = Boolean.TRUE;
						vo.getSecaoConfExames().setIndImprimir(vo.getImpressao());
					}
					
					if(vo.getObrigatorio()){
						if(alterouSecaoObrigatoria(vo)){
							algumFoiAlterado = Boolean.TRUE;
							if(vo.getSecaoObrigatoria()!= null && vo.getSecaoObrigatoria().equals(DominioWorkflowExamePatologia.MC)){
								vo.getSecaoConfExames().setEtapaLaudo(DominioSituacaoExamePatologia.MC);
							}else if(vo.getSecaoObrigatoria()!=null && vo.getSecaoObrigatoria().equals(DominioWorkflowExamePatologia.TC)){
								vo.getSecaoConfExames().setEtapaLaudo(DominioSituacaoExamePatologia.TC);
							}else{
								vo.getSecaoConfExames().setEtapaLaudo(null);
							}
						}
					}
					
				}
			}
			
			if(!algumFoiAlterado){
				throw new ApplicationBusinessException(AelSecaoConfExameRNExceptionCode.NENHUM_REGISTRO_ALTERADO_MANTER_CARACTERISTICA_MATERIAL_ANALISE);
			} else {
				//this.persistirAlteracoes(lista, lu2Seq, servidor);
				for(SecaoConfExameVO vo : lista){
					AelSecaoConfExames criarNovoRegistro = this.criarNovoRegistro(vo.getSecaoConfExames(), lu2Seq, servidor);
					this.persistir(criarNovoRegistro);
				}
			} 
		} else {
			throw new ApplicationBusinessException(AelSecaoConfExameRNExceptionCode.NENHUMA_SECAO_INFORMADA_MANTER_CARACTERISTICA_MATERIAL_ANALISE);
		}
	}
	
	private Boolean validarAlgumMarcado(List<SecaoConfExameVO> lista) throws ApplicationBusinessException{
		Boolean algumEstaMarcado = Boolean.FALSE;
		for (SecaoConfExameVO vo : lista) {
			if(vo.getAtivo()){
				algumEstaMarcado = Boolean.TRUE;
				break;
			}
		}
		
		return algumEstaMarcado;
	}
	
	/**
	 * Método utilizado no xhtml para habilitar e desabilitar Secao Obrigatória.
	 * @param vo
	 * @return
	 */
	private Boolean alterouSecaoObrigatoria(SecaoConfExameVO vo) {
		if(vo.getSecaoObrigatoria()!=null && vo.getSecaoConfExames().getEtapaLaudo()!=null && !vo.getSecaoObrigatoria().getDominioSituacaoExamePatologia().equals(vo.getSecaoConfExames().getEtapaLaudo())){
			return true;
		}
		
		if((vo.getSecaoObrigatoria() == null && vo.getSecaoConfExames().getEtapaLaudo()!=null) || (vo.getSecaoObrigatoria() != null && vo.getSecaoConfExames().getEtapaLaudo()==null)){
			return true;
		}
		return false;
	}
	
	private AelSecaoConfExames criarNovoRegistro(AelSecaoConfExames itemAserClonado, Integer lu2Seq, RapServidores servidor){
		AelSecaoConfExames clone = new AelSecaoConfExames();
		clone.setAba(itemAserClonado.getAba());
		clone.setAelConfigExLaudoUnico(this.getAelConfigExLaudoUnicoDAO().obterOriginal(lu2Seq));
		clone.setCriadoEm(new Date());
		clone.setDescricao(itemAserClonado.getDescricao());
		clone.setIndSituacao(itemAserClonado.getIndSituacao());
		clone.setVersaoConf((itemAserClonado.getVersaoConf() + 1));
		if(itemAserClonado.getIndSituacao().equals(DominioSituacao.I)){
			clone.setIndObrigatorio(Boolean.FALSE);
			clone.setIndImprimir(Boolean.FALSE);
			clone.setEtapaLaudo(null);
		}else{
			clone.setIndObrigatorio(itemAserClonado.getIndObrigatorio());
			clone.setIndImprimir(itemAserClonado.getIndImprimir());
		}
		
		if(itemAserClonado.getIndObrigatorio()){
			clone.setEtapaLaudo(itemAserClonado.getEtapaLaudo());
		}else{
			clone.setEtapaLaudo(null);
		}
		clone.setRapServidor(servidor);
		return clone;
	}
	
	private Boolean validarAlteracaoNaSituacao(SecaoConfExameVO vo){
		
		Boolean situacaoAtual = vo.getAtivo();
		DominioSituacao situacaoAnterior = vo.getSecaoConfExames().getIndSituacao();
		
		if((situacaoAtual && !DominioSituacao.A.equals(situacaoAnterior)) //se estava false e foi pra true 
				|| !situacaoAtual && DominioSituacao.A.equals(situacaoAnterior)){ // se estava true e foi pra false
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	private Boolean validarAlteracaoIndObrigatorio(SecaoConfExameVO vo){
		
		Boolean situacaoAtual = vo.getObrigatorio()!=null?vo.getObrigatorio():Boolean.FALSE;
		Boolean situacaoAnterior = vo.getSecaoConfExames()!=null?vo.getSecaoConfExames().getIndObrigatorio():Boolean.FALSE;
		
		if(!situacaoAtual.equals(situacaoAnterior)){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	private Boolean validarAlteracaoIndImpressao(SecaoConfExameVO vo){
		
		Boolean situacaoAtual = vo.getImpressao()!=null?vo.getImpressao():Boolean.FALSE;
		Boolean situacaoAnterior = vo.getSecaoConfExames().getIndImprimir()!=null?vo.getSecaoConfExames().getIndImprimir():Boolean.FALSE;
		
		if(!situacaoAtual.equals(situacaoAnterior)){
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	public void persistir(AelSecaoConfExames novoRegistro) {
		getAelSecaoConfExamesDAO().persistir(novoRegistro);
	}
	
	protected AelSecaoConfExamesDAO getAelSecaoConfExamesDAO() {
		return this.aelSecaoConfExamesDAO;
	}
	
	private AelConfigExLaudoUnicoDAO getAelConfigExLaudoUnicoDAO(){
		return this.aelConfigExLaudoUnicoDAO;
	}
}