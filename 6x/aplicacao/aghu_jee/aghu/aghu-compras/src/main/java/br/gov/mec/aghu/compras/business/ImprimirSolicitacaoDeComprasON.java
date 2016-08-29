package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.RelatoriosScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.dao.ScoArquivoAnexoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.SolicitacaoDeComprasVO;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class ImprimirSolicitacaoDeComprasON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ImprimirSolicitacaoDeComprasON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;

@Inject
private RelatoriosScoSolicitacoesDeComprasDAO relatoriosScoSolicitacoesDeComprasDAO;
	
@Inject
private ScoArquivoAnexoDAO scoArquivoAnexoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6507810139449684256L;

	public enum SolicitacaodeComprasONExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
	}	
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<SolicitacaoDeComprasVO> pesquisaDadosSolicitacaoCompras(List<Integer> numSolicComp)
	throws ApplicationBusinessException{
		
		List<SolicitacaoDeComprasVO> listarSolicitacaoDeCompras = getScoSolicitacoesDeComprasDAO().listarSolicitacoesDeComprasPorNumero(numSolicComp);
		
		
		
		if( (listarSolicitacaoDeCompras.size()== 0 || listarSolicitacaoDeCompras.isEmpty()) ){
			throw new ApplicationBusinessException(SolicitacaodeComprasONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		//SolicitacaoDeComprasVO solicitacaoDeComprasVO =  listarSolicitacaoDeCompras.get(0);
		for (SolicitacaoDeComprasVO scoVO : listarSolicitacaoDeCompras){
		
			// Aplicacao
			if (scoVO.getPacNome() == null){
				scoVO.setNewAplicacao(scoVO.getAplicacao());
			}else{
				scoVO.setNewAplicacao(scoVO.getProntuario() + " - " + scoVO.getPacNome());
			}
			
			// Data Alteracao
			if (scoVO.getDataAlt() == null){
				scoVO.setNewDataAlt(null);
			}else{
				scoVO.setNewDataAlt("Data Alt. :" + 
						scoVO.getDataAlt().toString());
			}
			
			//Responsavel 1
			if (scoVO.getSerVinCodigoAlterada() != null && 
					scoVO.getSerMatriculaAlterada() != null) {
				
				Short codigo = scoVO.getSerVinCodigoAlterada();
				Integer matricula = scoVO.getSerMatriculaAlterada();
				
				scoVO.setResponsavel("Resp.     : " + 
						getRelatoriosScoSolicitacoesDeComprasDAO().responsavel(codigo, matricula).toString());
	
			}else{
				scoVO.setResponsavel(null);
			}
			
			//Total Valor Previsto
			if (scoVO.getTotalValorPrevisto() == null && scoVO.getValorUnitPrevisto()!=null){
				
				Long temp = scoVO.getQtdAprov();
				
				int i = temp.intValue();  
				BigDecimal QuantidadeAprov = new BigDecimal(i); 
				
				scoVO.setTotalValorPrevisto(scoVO.getValorUnitPrevisto()
						.multiply(QuantidadeAprov));
			}else{
				scoVO.setTotalValorPrevisto(null);
			}
			
			//Responsavel 2
			if (scoVO.getSerVinCodigoOrcamento() != null && 
					scoVO.getSerMatriculaOrcamento() != null) {
				
				Short codigo = scoVO.getSerVinCodigoOrcamento();
				Integer matricula = scoVO.getSerMatriculaOrcamento();
				
				scoVO.setResponsavel2(getRelatoriosScoSolicitacoesDeComprasDAO().responsavel(codigo, matricula).toString());
			}else{
				scoVO.setResponsavel2(null);
			}
	
			//Numero AF
			if (scoVO.getNroAF() == null){
				String AF1 = null;
				String AF2 = null;
				if (getRelatoriosScoSolicitacoesDeComprasDAO().numeroAF1(scoVO.getNroSolicitacao()) != null){
					AF1 = getRelatoriosScoSolicitacoesDeComprasDAO().numeroAF1(scoVO.getNroSolicitacao()).toString();
				}else{
					AF1 = null;
				}
				if (getRelatoriosScoSolicitacoesDeComprasDAO().numeroAF2(scoVO.getNroSolicitacao()) != null){
					AF2 = getRelatoriosScoSolicitacoesDeComprasDAO().numeroAF2(scoVO.getNroSolicitacao()).toString();
				}else{
					AF2 = null;
				}
				if (AF1 != null && AF2 != null){
					scoVO.setNroAF(AF1.concat(" / ").concat(AF2));
				}else{
					scoVO.setNroAF(null);
				}
			}
			
			//Ult. NR
			if (scoVO.getUltimoNR() == null){
				scoVO.setUltimoNR(getScoSolicitacoesDeComprasDAO().ultNR(scoVO.getNroSolicitacao()));
			}else{
				scoVO.setUltimoNR(null);
			}
			
			//Data Entrada
			if (scoVO.getDataEntrada() == null){
				scoVO.setDataEntrada(getRelatoriosScoSolicitacoesDeComprasDAO().dtEntrada(scoVO.getNroSolicitacao()));
			}else{
				scoVO.setDataEntrada(null);
			}
			
			//Endereco
			if (scoVO.getEnd() == null){
				String ende1 = getRelatoriosScoSolicitacoesDeComprasDAO().endereco(scoVO.getNroSolicitacao());
				String ende2 = getRelatoriosScoSolicitacoesDeComprasDAO().endereco2(scoVO.getNroSolicitacao());
				
				if (ende1 != null && ende2 != null){
					scoVO.setEnd(ende1);
				}else{
					if (ende1 != null || ende2 != null){
						if (ende1 != null && ende2 == null){
							scoVO.setEnd(ende1);
						}
						if (ende1 == null && ende2 != null){
							scoVO.setEnd(ende2);
						}
					}else{
						scoVO.setEnd(null);
					}
				}
			}
			scoVO.setPossuiAnexo(getScoArquivoAnexoDAO().verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento.SC, scoVO.getNroSolicitacao()));
		}
		return listarSolicitacaoDeCompras;

	}

	private ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}
	
	private RelatoriosScoSolicitacoesDeComprasDAO getRelatoriosScoSolicitacoesDeComprasDAO() {
		return relatoriosScoSolicitacoesDeComprasDAO;
	}
	
	private ScoArquivoAnexoDAO getScoArquivoAnexoDAO() {
		return scoArquivoAnexoDAO;
	}
}
