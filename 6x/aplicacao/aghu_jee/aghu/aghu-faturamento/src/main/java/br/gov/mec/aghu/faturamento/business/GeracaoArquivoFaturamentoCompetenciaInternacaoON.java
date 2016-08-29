package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class GeracaoArquivoFaturamentoCompetenciaInternacaoON
		extends
		BaseBMTBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5115109213216805717L;
	
	private static final Log LOG = LogFactory.getLog(GeracaoArquivoFaturamentoCompetenciaInternacaoON.class);
	
	public final static int TRANSACTION_TIMEOUT_24_HORAS = 60 * 60 * 24; //= 1 dia 
	
	public final static int QTD_ITENS_MAX_CLAUSE_IN = 998;
	
	@EJB
	private ContaHospitalarON contaHospitalarON;
	
	@EJB
	private GeracaoArquivoFaturamentoCompetenciaInternacaoRN geracaoArquivoFaturamentoCompetenciaInternacaoRN;
	
	@EJB
	private FatCompetenciaRN fatCompetenciaRN;
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@Inject 
	private RapServidoresDAO rapServidoresDAO;
	
	@EJB
	private FatkCthRN fatkCthRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	protected GeracaoArquivoFaturamentoCompetenciaInternacaoRN getGeracaoArquivoFaturamentoCompetenciaInternacaoRN() {

		return geracaoArquivoFaturamentoCompetenciaInternacaoRN;
	}

	public void gerarCompetenciaEmManutencao(
			final FatCompetencia competencia, final String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		 getGeracaoArquivoFaturamentoCompetenciaInternacaoRN().gerarCompetenciaEmManutencao(competencia, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * 
	 * ORADB Procedure <code>FATF_ATU_COMP_INT.EVT_ENCERRA_COMPETENCIA</code>
	 * @param fatCompetencia
	 * @throws BaseException 
	 */
	public Boolean encerrarCompetenciaAtualEAbreNovaCompetencia(final FatCompetencia fatCompetencia, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		if(DominioSituacaoCompetencia.M.equals(fatCompetencia.getIndSituacao())){
			this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
			try{
				try {
					// Aciona função (procedure) para reabrir conta não autorizadas (reabre_contas_nao_autorizadas);
					rnCthcAtuReabreEmLote(nomeMicrocomputador, dataFimVinculoServidor);
				} catch (BaseException e) {
					logError("Erros na execução fatk_cth_rn.rn_cthc_atu_reabre em "+ new Date());
				}				
				// Atualiza tabela fat_competencias para situação "P" (ind_situacao = "P") (Apresentada) e ind_faturado = "S"
				FatCompetencia fatCompetenciaAtualizar = getFatCompetenciaDAO().obterCompetenciaModuloMesAno(fatCompetencia.getId().getModulo(), fatCompetencia.getId().getMes(), fatCompetencia.getId().getAno());
				fatCompetenciaAtualizar.setIndSituacao(DominioSituacaoCompetencia.P);
				fatCompetenciaAtualizar.setIndFaturado(true);
				getFatCompetenciaRN().atualizarFatCompetencia(fatCompetenciaAtualizar);
				
				//  Atualiza contas hospitalares com situação de cobrada ("O") para as contas da competência 
				//  que estão encerradas, conforme a seguinte consulta:
				RapServidores servidorLogado = rapServidoresDAO.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
				
				List<FatContasHospitalares> listaContasHospitalares = getFatContasHospitalaresDAO().obterContasHospitalaresCobradasParaCompetenciasEncerradas(fatCompetencia);

				List<String> listCodigoDCHI = new  ArrayList<>();
				for(final FatContasHospitalares cth : listaContasHospitalares ){
					listCodigoDCHI.add(cth.getDocumentoCobrancaAih().getCodigo());
					
					if(listCodigoDCHI.size() == QTD_ITENS_MAX_CLAUSE_IN){
						getFaturamentoFacade().persistirContasCobradasEmLote(listCodigoDCHI, nomeMicrocomputador, servidorLogado);
						listCodigoDCHI = new  ArrayList<>();
					}
					
				}
				if(listCodigoDCHI.size() > 0){
					getFaturamentoFacade().persistirContasCobradasEmLote(listCodigoDCHI, nomeMicrocomputador, servidorLogado);
				}
				
			}catch (Exception e){
				logError("Exceção capturada: ", e);
				this.rollbackTransaction();
				throw new ApplicationBusinessException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
			}
			this.commitTransaction();
			return Boolean.TRUE;
			
		} else {
			return Boolean.FALSE;
		}
	}

	public void rnCthcAtuReabreEmLote(String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		
		DominioSituacaoCompetencia vSitCpe = null;
		List<Integer> contas = getFatContasHospitalaresDAO().obterFatContasHospitalaresNaoAutorizadas();
		
		if(contas != null && !contas.isEmpty()) {
			getFatkCthRN().removerPorContaHospitalar(contas);
			for (Integer cthSeq : contas) {

				vSitCpe = getFatkCthRN().atualizaContaHospitalar(nomeMicrocomputador,
						servidorLogado, vSitCpe, cthSeq);
			}
		}
	}
	
	private FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}

	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO(){
		return fatContasHospitalaresDAO;
	}
	
	protected ContaHospitalarON getContaHospitalarON(){
		return contaHospitalarON;
	}
	
	protected FatCompetenciaRN getFatCompetenciaRN(){
		return fatCompetenciaRN;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	private FatkCthRN getFatkCthRN(){
		return fatkCthRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
}