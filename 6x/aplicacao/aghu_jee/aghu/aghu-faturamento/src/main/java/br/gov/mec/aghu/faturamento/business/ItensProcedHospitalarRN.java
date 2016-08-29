package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioModoLancamentoFat;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarJnDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatItemProcedHospitalarJn;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * TODO Remover e substituir chamadas por {@link VerificacaoItemProcedimentoHospitalarRN}
 * @author fgka
 *
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class ItensProcedHospitalarRN extends BaseBusiness implements Serializable {

	@EJB
	private TipoCaracteristicaItemRN tipoCaracteristicaItemRN;
	
	private static final Log LOG = LogFactory.getLog(ItensProcedHospitalarRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;
	
	@Inject
	private FatCompatExclusItemDAO fatCompatExclusItemDAO;
	
	@Inject
	private FatTipoCaractItensDAO fatTipoCaractItensDAO;
	
	@Inject
	private FatItensProcedHospitalarJnDAO fatItensProcedHospitalarJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4563004648841195988L;

	/**
	 * ORADB: Function FATK_IPH_RN.RN_IPHC_VER_DIAACOMP
	 */
	public boolean verificaDiariaAcompanhante(FatItensProcedHospitalar itemProcedHospitalar) {
		return itemProcedHospitalar.getDiariaAcompanhante();
	}
	
	/** ORADB: FATT_IPH_ARD ON FAT_ITENS_PROCED_HOSPITALAR
	 * 
	 * @param itemProcedHospitalar
	 *  
	 */
	public void executarAposDeletarItemProcedHospitalar(FatItensProcedHospitalar itemProcedHospitalar) throws BaseException {
		this.inserirJournalItemProcedHospitalar(itemProcedHospitalar, DominioOperacoesJournal.DEL);
	}
	
	/**
	 * 
	 * @param itemProcedHospitalar
	 *  
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void inserirJournalItemProcedHospitalar(FatItensProcedHospitalar iph, DominioOperacoesJournal operacao) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
//		AghuFacade aghuFacade = this.getAghuFacade();
		
		FatItemProcedHospitalarJn jn = new FatItemProcedHospitalarJn(); 

		jn.setNomeUsuario(servidorLogado.getUsuario());
		jn.setOperacao(operacao);
		jn.setPhoSeq(iph.getProcedimentoHospitalar().getSeq());
		jn.setSeq(iph.getId().getSeq());
		jn.setCodTabela(iph.getCodTabela());
		jn.setAlteradoEm(new Date());
		jn.setDescricao(iph.getDescricao());
		jn.setIdadeMin(iph.getIdadeMin().byteValue());
		jn.setIdadeMax(iph.getIdadeMax().shortValue());
		jn.setSexo(iph.getSexo() != null ? iph.getSexo().toString() : null);
		jn.setClcCodigo(iph.getClinica() != null ? iph.getClinica().getCodigo().byteValue() : null);
		jn.setIndHospDia(iph.getHospDia());
		jn.setDiasPermanenciaMaior(iph.getDiasPermanenciaMaior());
		jn.setQuantDiasFaturamento(iph.getQuantDiasFaturamento());
		jn.setIndInternacao(iph.getInternacao());
		jn.setPontoAnestesista(iph.getPontoAnestesista());
		jn.setPontosCirurgiao(iph.getPontosCirurgiao());
		jn.setIndSituacao(iph.getSituacao());
		jn.setIndExigeValor(iph.getExigeValor());
		jn.setCriadoPor(iph.getCriadoPor());
		jn.setCriadoEm(iph.getCriadoEm());
		jn.setAlteradoPor(iph.getAlteradoPor());
		jn.setAlteradoEm(iph.getAlteradoEm());
		jn.setMaxDiariaUti(iph.getMaxDiariaUti());
		jn.setIndCobrancaCma(iph.getCobrancaCma());
		jn.setIndExigeAutorizacaoPrevia(iph.getExigeAutorizacaoPrevia());
		jn.setIndProcEspecial(iph.getProcedimentoEspecial());
		jn.setIndDiariaAcomp(iph.getDiariaAcompanhante());
		jn.setDiasDesdobramento(iph.getDiasDesdobramento());
		jn.setMaxQtdConta(iph.getMaxQtdConta());
		jn.setIndRealDifereSolic(iph.getRealDifereSolic());
		jn.setIndCidObrigatorio(iph.getCidadeObrigatoria());
		jn.setIndDadosParto(iph.getDadosParto());
		jn.setIndHcpaCadastrado(iph.getHcpaCadastrado());
		jn.setIphSeq(iph.getIphSeq());
		jn.setPercServicoProfissional(iph.getPercServicoProfissional());
		jn.setIndCobraProcEspecial(iph.getCobraProcedimentoEspecial());
		jn.setIndQtdMaiorInternacao(iph.getQuantidadeMaiorInternacao());
		jn.setDiasReinternacao(iph.getDiasReinternacao());
		jn.setMaxQtdApac(iph.getMaxQtdApac());
		jn.setIndTipoAih5(iph.getTipoAih5());
		jn.setIndCobrancaConta(iph.getCobrancaConta());
		jn.setIndCobrancaApac(iph.getCobrancaApac());
		jn.setIndPreencheCma(iph.getPreencheCma());
		jn.setIndUtilizacaoItem(iph.getUtilizacaoItem());
		jn.setQtdProcedimentosItem(iph.getQtdProcedimentosItem());
		jn.setTivSeq(iph.getTiposVinculo()!=null?iph.getTiposVinculo().getSeq():null);
		jn.setTaoSeq(iph.getTipoAto()!=null?iph.getTipoAto().getSeq():null);
		jn.setPontosSadt(iph.getPontosSadt());
		jn.setIndConsulta(iph.getConsulta());
		jn.setIndCobraExcedenteBpa(iph.getCobraExcedenteBpa());
		jn.setIndCobrancaAmbulatorio(iph.getCobrancaAmbulatorio());
		jn.setIndCirurgiaMultipla(iph.getCirurgiaMultipla());
		jn.setIndPsiquiatria(iph.getPsiquiatria());
		jn.setIndAidsPolitraumatizado(iph.getAidsPolitraumatizado());
		jn.setIndFaec(iph.getFaec());
		jn.setIndFideps(iph.getFideps());
		jn.setIndDcihTransplante(iph.getDcihTransplante());
		jn.setIndSolicDifereReal(iph.getSolicDifereReal());
		jn.setIndExigeConsulta(iph.getExigeConsulta());
		jn.setIndBuscaDoador(iph.getBuscaDoador());
		jn.setIndCobrancaDiarias(iph.getCobrancaDiarias());
		jn.setMcaSeq(iph.getMotivoCobrancaApac()!=null?iph.getMotivoCobrancaApac().getSeq():null);
		jn.setNotaFiscalFalsa(iph.getNotaFiscalFalsa());
		jn.setIndProcPrincipalApac(iph.getProcPrincipalApac());
		jn.setIndModoLancamentoFat(iph.getModoLancamentoFat());
		jn.setSicCodigo(iph.getSicCodigo());
		jn.setGitCodigo(iph.getGitCodigo());
		jn.setFccSeq(iph.getCaracteristicaComplexidade()!=null?iph.getCaracteristicaComplexidade().getSeq():null);
		jn.setFcfSeq(iph.getFatCaracteristicaFinanciamento() != null ? iph.getFatCaracteristicaFinanciamento().getSeq() : null );

		this.getFatItensProcedHospitalarJnDAO().persistir(jn);
	}

	/** ORADB: FATT_IPH_ARU ON FAT_ITENS_PROCED_HOSPITALAR
	 * 
	 * @param itemProcedHospitalar
	 *  
	 */
	public void executarAposAtualizarItemProcedHospitalar(FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph) throws BaseException {
		
		if(
			(CoreUtil.modificados(newIph.getId(), oldIph.getId()))
			||(CoreUtil.modificados(newIph.getCodTabela(), oldIph.getCodTabela()))
			||(CoreUtil.modificados(newIph.getDescricao(), oldIph.getDescricao()))
			||(CoreUtil.modificados(newIph.getIdadeMax(), oldIph.getIdadeMax()))
			||(CoreUtil.modificados(newIph.getIdadeMin(), oldIph.getIdadeMin()))
			||(CoreUtil.modificados(newIph.getSexo(), oldIph.getSexo()))
			||(CoreUtil.modificados(newIph.getClinica(), oldIph.getClinica()))
			||(CoreUtil.modificados(newIph.getHospDia(), oldIph.getHospDia()))
			||(CoreUtil.modificados(newIph.getDiasPermanenciaMaior(), oldIph.getDiasPermanenciaMaior()))
			||(CoreUtil.modificados(newIph.getQuantDiasFaturamento(), oldIph.getQuantDiasFaturamento()))
			||(CoreUtil.modificados(newIph.getInternacao(), oldIph.getInternacao()))
			||(CoreUtil.modificados(newIph.getPontoAnestesista(), oldIph.getPontoAnestesista()))
			||(CoreUtil.modificados(newIph.getPontosCirurgiao(), oldIph.getPontosCirurgiao()))
			||(CoreUtil.modificados(newIph.getSituacao(), oldIph.getSituacao()))
			||(CoreUtil.modificados(newIph.getExigeValor(), oldIph.getExigeValor()))
			||(CoreUtil.modificados(newIph.getCriadoPor(), oldIph.getCriadoPor()))
			||(CoreUtil.modificados(newIph.getCriadoEm(), oldIph.getCriadoEm()))
			||(CoreUtil.modificados(newIph.getAlteradoPor(), oldIph.getAlteradoPor()))
			||(CoreUtil.modificados(newIph.getAlteradoEm(), oldIph.getAlteradoEm()))
			||(CoreUtil.modificados(newIph.getMaxDiariaUti(), oldIph.getMaxDiariaUti()))
			||(CoreUtil.modificados(newIph.getCobrancaCma(), oldIph.getCobrancaCma()))
			||(CoreUtil.modificados(newIph.getExigeAutorizacaoPrevia(), oldIph.getExigeAutorizacaoPrevia()))
			||(CoreUtil.modificados(newIph.getProcedimentoEspecial(), oldIph.getProcedimentoEspecial()))
			||(CoreUtil.modificados(newIph.getDiariaAcompanhante(), oldIph.getDiariaAcompanhante()))
			||(CoreUtil.modificados(newIph.getDiasDesdobramento(), oldIph.getDiasDesdobramento()))
			||(CoreUtil.modificados(newIph.getMaxQtdConta(), oldIph.getMaxQtdConta()))
			||(CoreUtil.modificados(newIph.getRealDifereSolic(), oldIph.getRealDifereSolic()))
			||(CoreUtil.modificados(newIph.getCidadeObrigatoria(), oldIph.getCidadeObrigatoria()))
			||(CoreUtil.modificados(newIph.getDadosParto(), oldIph.getDadosParto()))
			||(CoreUtil.modificados(newIph.getHcpaCadastrado(), oldIph.getHcpaCadastrado()))
			||(CoreUtil.modificados(newIph.getIphSeq(), oldIph.getIphSeq()))//###### VER #######
			||(CoreUtil.modificados(newIph.getPercServicoProfissional(), oldIph.getPercServicoProfissional()))
			||(CoreUtil.modificados(newIph.getCobraProcedimentoEspecial(), oldIph.getCobraProcedimentoEspecial()))
			||(CoreUtil.modificados(newIph.getQuantidadeMaiorInternacao(), oldIph.getQuantidadeMaiorInternacao()))
			||(CoreUtil.modificados(newIph.getDiasReinternacao(), oldIph.getDiasReinternacao()))
			||(CoreUtil.modificados(newIph.getMaxQtdApac(), oldIph.getMaxQtdApac()))
			||(CoreUtil.modificados(newIph.getTipoAih5(), oldIph.getTipoAih5()))
			||(CoreUtil.modificados(newIph.getCobrancaConta(), oldIph.getCobrancaConta()))
			||(CoreUtil.modificados(newIph.getCobrancaApac(), oldIph.getCobrancaApac()))
			||(CoreUtil.modificados(newIph.getPreencheCma(), oldIph.getPreencheCma()))
			||(CoreUtil.modificados(newIph.getUtilizacaoItem(), oldIph.getUtilizacaoItem()))
			||(CoreUtil.modificados(newIph.getQtdProcedimentosItem(), oldIph.getQtdProcedimentosItem()))
			||(CoreUtil.modificados(newIph.getTiposVinculo(), oldIph.getTiposVinculo()))
			||(CoreUtil.modificados(newIph.getTipoAto(), oldIph.getTipoAto()))
			||(CoreUtil.modificados(newIph.getPontosSadt(), oldIph.getPontosSadt()))
			||(CoreUtil.modificados(newIph.getConsulta(), oldIph.getConsulta()))
			||(CoreUtil.modificados(newIph.getCobraExcedenteBpa(), oldIph.getCobraExcedenteBpa()))
			||(CoreUtil.modificados(newIph.getCobrancaAmbulatorio(), oldIph.getCobrancaAmbulatorio()))
			||(CoreUtil.modificados(newIph.getCirurgiaMultipla(), oldIph.getCirurgiaMultipla()))
			||(CoreUtil.modificados(newIph.getPsiquiatria(), oldIph.getPsiquiatria()))
			||(CoreUtil.modificados(newIph.getAidsPolitraumatizado(), oldIph.getAidsPolitraumatizado()))
			||(CoreUtil.modificados(newIph.getFaec(), oldIph.getFaec()))
			||(CoreUtil.modificados(newIph.getFideps(), oldIph.getFideps()))
			||(CoreUtil.modificados(newIph.getDcihTransplante(), oldIph.getDcihTransplante()))
			||(CoreUtil.modificados(newIph.getSolicDifereReal(), oldIph.getSolicDifereReal()))
			||(CoreUtil.modificados(newIph.getExigeConsulta(), oldIph.getExigeConsulta()))
			||(CoreUtil.modificados(newIph.getBuscaDoador(), oldIph.getBuscaDoador()))
			||(CoreUtil.modificados(newIph.getCobrancaDiarias(), oldIph.getCobrancaDiarias()))
			||(CoreUtil.modificados(newIph.getMotivoCobrancaApac(), oldIph.getMotivoCobrancaApac()))
			||(CoreUtil.modificados(newIph.getNotaFiscalFalsa(), oldIph.getNotaFiscalFalsa()))
			||(CoreUtil.modificados(newIph.getProcPrincipalApac(), oldIph.getProcPrincipalApac()))
			||(CoreUtil.modificados(newIph.getModoLancamentoFat(), oldIph.getModoLancamentoFat()))
			||(CoreUtil.modificados(newIph.getSicCodigo(), oldIph.getSicCodigo()))
			||(CoreUtil.modificados(newIph.getGitCodigo(), oldIph.getGitCodigo()))
			||(CoreUtil.modificados(newIph.getCaracteristicaComplexidade(), oldIph.getCaracteristicaComplexidade()))
			||(CoreUtil.modificados(newIph.getFatCaracteristicaFinanciamento(), oldIph.getFatCaracteristicaFinanciamento()))
		){
			this.inserirJournalItemProcedHospitalar(oldIph, DominioOperacoesJournal.UPD);
		}
		}

	/** ORADB: FATT_IPH_ASI ON FAT_ITENS_PROCED_HOSPITALAR
	 * 
	 * @param itemProcedHospitalar
	 */
	public void executarStatementAposInserirItemProcedHospitalar(FatItensProcedHospitalar newIph){
		
		//fatK_iph.process_iph_rows('INSERT');
		this.executarProcessItemProcedHospitalar(newIph, null, DominioOperacoesJournal.INS);
	}

	
	/** ORADB: FATT_IPH_ASU ON FAT_ITENS_PROCED_HOSPITALAR
	 * 
	 * @param itemProcedHospitalar
	 */
	public void executarStatementAposAtualizarItemProcedHospitalar(FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph){
		
		//fatK_iph.process_iph_rows('UPDATE');
		this.executarProcessItemProcedHospitalar(newIph, oldIph, DominioOperacoesJournal.UPD);
	}
	
	
	/**
	 * ORADB FATK_IPH.PROCESS_IPH_ROWS
	 * @param newIph
	 * @param oldIph
	 * @param operacao
	 */
	public void executarProcessItemProcedHospitalar(FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph, DominioOperacoesJournal operacao){
		if(!DominioOperacoesJournal.DEL.equals(operacao)){
			//atP_enforce_iph_rules(l_iph_saved_row, l_iph_row_new, p_event);
			this.executarEnforceItemProdecHospitalar(newIph, oldIph, operacao);
		}
	}
	
	
	/**
	 * ORADB FATP_ENFORCE_IPH_RULES
	 * @param newIph
	 * @param oldIph
	 * @param operacao
	 * 
	 * A implementacao desta migracao esta com alguns detalhes diferentes do código original, 
	 * conforme definição do analista.
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount", "PMD.NPathComplexity"})
	public void executarEnforceItemProdecHospitalar(FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph, DominioOperacoesJournal operacao){
		
		if(DominioOperacoesJournal.UPD.equals(operacao)){
			if(CoreUtil.modificados(newIph.getMaxQtdApac(),oldIph.getMaxQtdApac())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.QTD_MAXIMA_COBRAVEL_APAC);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);
				
				FatCaractItemProcHosp ciph = fatCaractItemProcHospDAO.obterPorChavePrimaria(id);
				if(newIph.getMaxQtdApac() == null || newIph.getMaxQtdApac().equals((short)0)){
					if(ciph != null){
						newIph.getCaracteristicasItemProcHosp().remove(ciph);
						fatCaractItemProcHospDAO.remover(ciph);
					}
				}else if(oldIph.getMaxQtdApac() == null || oldIph.getMaxQtdApac().equals((short)0)){
					//Remove
					FatTipoCaractItens tipoCaractItem;
					if(ciph != null){
						tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(ciph.getTipoCaracteristicaItem());
						newIph.getCaracteristicasItemProcHosp().remove(ciph);
						fatCaractItemProcHospDAO.remover(ciph);
					}
					else {
						tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
					}
					//Insere
					FatCaractItemProcHosp ciphAux = new FatCaractItemProcHosp();
					ciphAux.setId(id);
					ciphAux.setValorNumerico(newIph.getMaxQtdApac().intValue());
					ciphAux.setTipoCaracteristicaItem(tipoCaractItem);
					fatCaractItemProcHospDAO.persistir(ciphAux);
					newIph.getCaracteristicasItemProcHosp().add(ciphAux);
					
				}else{
					ciph.setValorNumerico(newIph.getMaxQtdApac() != null ? newIph.getMaxQtdApac().intValue() : null);
					//fatCaractItemProcHospDAO.atualizar(ciph);Atualizar no final, quando executar o flush
				}
			}
			
			if(CoreUtil.modificados(newIph.getModoLancamentoFat(),oldIph.getModoLancamentoFat())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.MODO_LANCAMENTO_FAT);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);
				FatCaractItemProcHosp ciph = fatCaractItemProcHospDAO.obterPorChavePrimaria(id);
				if(DominioModoLancamentoFat.O.equals(newIph.getModoLancamentoFat())){
					if(ciph != null){
						newIph.getCaracteristicasItemProcHosp().remove(ciph);
						fatCaractItemProcHospDAO.remover(ciph);
					}
				}else if(DominioModoLancamentoFat.O.equals(oldIph.getModoLancamentoFat())){
					FatTipoCaractItens tipoCaractItem;
					if(ciph != null){
						tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(ciph.getTipoCaracteristicaItem());
						newIph.getCaracteristicasItemProcHosp().remove(ciph);
						fatCaractItemProcHospDAO.remover(ciph);
					}
					else {
						tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
					}
					
					FatCaractItemProcHosp ciphAux = new FatCaractItemProcHosp();
					ciphAux.setId(id);
					ciphAux.setValorChar(newIph.getModoLancamentoFat() != null ? newIph.getModoLancamentoFat().toString() : null);
					ciphAux.setTipoCaracteristicaItem(tipoCaractItem);
					fatCaractItemProcHospDAO.persistir(ciphAux);
					newIph.getCaracteristicasItemProcHosp().add(ciphAux);
					
				}else{
					ciph.setValorChar(newIph.getModoLancamentoFat() != null ? newIph.getModoLancamentoFat().toString() : null);
					//fatCaractItemProcHospDAO.atualizar(ciph);Atualizar no final, quando executar o flush
				}
			}

			if(CoreUtil.modificados(newIph.getCobraExcedenteBpa(),oldIph.getCobraExcedenteBpa())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.COBRA_EXCEDENTE_BPA);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);
				FatCaractItemProcHosp ciph = fatCaractItemProcHospDAO.obterPorChavePrimaria(id);

				FatTipoCaractItens tipoCaractItem;
				if(ciph != null){
					tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(ciph.getTipoCaracteristicaItem());
					newIph.getCaracteristicasItemProcHosp().remove(ciph);
					fatCaractItemProcHospDAO.remover(ciph);
				}
				else {
					tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				}

				if(Boolean.TRUE.equals(newIph.getCobraExcedenteBpa())){
					FatCaractItemProcHosp ciphAux = new FatCaractItemProcHosp();
					ciphAux.setId(id);
					ciphAux.setValorChar(newIph.getCobraExcedenteBpa() != null ? (newIph.getCobraExcedenteBpa() ? "S" : "N") : null);
					ciphAux.setTipoCaracteristicaItem(tipoCaractItem);
					fatCaractItemProcHospDAO.persistir(ciphAux);
				}
			}

			if(CoreUtil.modificados(newIph.getCobrancaApac(),oldIph.getCobrancaApac())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.COBRA_APAC);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);
				FatCaractItemProcHosp ciph = fatCaractItemProcHospDAO.obterPorChavePrimaria(id);
				FatTipoCaractItens tipoCaractItem;
				if(ciph != null){
					tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(ciph.getTipoCaracteristicaItem());
					newIph.getCaracteristicasItemProcHosp().remove(ciph);
					fatCaractItemProcHospDAO.remover(ciph);
				}
				else {
					tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				}
				if(Boolean.TRUE.equals(newIph.getCobrancaApac())){
					FatCaractItemProcHosp ciphAux = new FatCaractItemProcHosp();
					ciphAux.setId(id);
					ciphAux.setValorChar(newIph.getCobrancaApac() != null ? (newIph.getCobrancaApac() ? "S" : "N") : null);
					ciphAux.setTipoCaracteristicaItem(tipoCaractItem);
					fatCaractItemProcHospDAO.persistir(ciphAux);
				}
			}

			if(CoreUtil.modificados(newIph.getProcPrincipalApac(),oldIph.getProcPrincipalApac())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.PROCEDIMENTO_PRINCIPAL_APAC);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);
				FatCaractItemProcHosp ciph = fatCaractItemProcHospDAO.obterPorChavePrimaria(id);
				FatTipoCaractItens tipoCaractItem;
				if(ciph != null){
					tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(ciph.getTipoCaracteristicaItem());
					newIph.getCaracteristicasItemProcHosp().remove(ciph);
					fatCaractItemProcHospDAO.remover(ciph);
				}
				else {
					tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				}
				if(Boolean.TRUE.equals(newIph.getProcPrincipalApac())){
					FatCaractItemProcHosp ciphAux = new FatCaractItemProcHosp();
					ciphAux.setId(id);
					ciphAux.setValorChar(newIph.getProcPrincipalApac() != null ? (newIph.getProcPrincipalApac() ? "S" : "N") : null);
					ciphAux.setTipoCaracteristicaItem(tipoCaractItem);
					fatCaractItemProcHospDAO.persistir(ciphAux);
				}
			}
			
			if(CoreUtil.modificados(newIph.getCobrancaAmbulatorio(),oldIph.getCobrancaAmbulatorio())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.COBRA_BPA);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);
				FatCaractItemProcHosp ciph = fatCaractItemProcHospDAO.obterPorChavePrimaria(id);
				FatTipoCaractItens tipoCaractItem;
				if(ciph != null){
					tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(ciph.getTipoCaracteristicaItem());
					newIph.getCaracteristicasItemProcHosp().remove(ciph);
					fatCaractItemProcHospDAO.remover(ciph);
				}
				else {
					tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				}
				if(Boolean.TRUE.equals(newIph.getCobrancaAmbulatorio())){
					FatCaractItemProcHosp ciphAux = new FatCaractItemProcHosp();
					ciphAux.setId(id);
					ciphAux.setValorChar(newIph.getCobrancaAmbulatorio() != null ? (newIph.getCobrancaAmbulatorio() ? "S" : "N") : null);
					ciphAux.setTipoCaracteristicaItem(tipoCaractItem);
					fatCaractItemProcHospDAO.persistir(ciphAux);
				}
			}
		} else if(DominioOperacoesJournal.INS.equals(operacao)){
			if(newIph.getMaxQtdApac() != null && !newIph.getMaxQtdApac().equals(0)){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.QTD_MAXIMA_COBRAVEL_APAC);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);

				FatTipoCaractItens tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				FatCaractItemProcHosp ciph = new FatCaractItemProcHosp();
				ciph.setId(id);
				ciph.setTipoCaracteristicaItem(tipoCaractItem);
				ciph.setValorNumerico(newIph.getMaxQtdApac().intValue());
				fatCaractItemProcHospDAO.persistir(ciph);
			}
			
			if(!DominioModoLancamentoFat.O.equals(newIph.getModoLancamentoFat())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.MODO_LANCAMENTO_FAT);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);

				FatTipoCaractItens tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				FatCaractItemProcHosp ciph = new FatCaractItemProcHosp();
				ciph.setId(id);
				ciph.setTipoCaracteristicaItem(tipoCaractItem);
				ciph.setValorChar(newIph.getModoLancamentoFat() != null ? newIph.getModoLancamentoFat().toString() : null);
				fatCaractItemProcHospDAO.persistir(ciph);
			}
			
			if(!Boolean.FALSE.equals(newIph.getCobraExcedenteBpa())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.COBRA_EXCEDENTE_BPA);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);
				
				FatTipoCaractItens tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				FatCaractItemProcHosp ciph = new FatCaractItemProcHosp();
				ciph.setId(id);
				ciph.setTipoCaracteristicaItem(tipoCaractItem);
				ciph.setValorChar(newIph.getCobraExcedenteBpa() != null ? (newIph.getCobraExcedenteBpa() ? "S" : "N") : null);
				fatCaractItemProcHospDAO.persistir(ciph);
			}

			if(!Boolean.FALSE.equals(newIph.getCobrancaApac())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.COBRA_APAC);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);
				
				FatTipoCaractItens tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				FatCaractItemProcHosp ciph = new FatCaractItemProcHosp();
				ciph.setId(id);
				ciph.setTipoCaracteristicaItem(tipoCaractItem);
				ciph.setValorChar(newIph.getCobraExcedenteBpa() != null ? (newIph.getCobraExcedenteBpa() ? "S" : "N") : null);
				fatCaractItemProcHospDAO.persistir(ciph);
			}

			if(!Boolean.FALSE.equals(newIph.getProcPrincipalApac())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.PROCEDIMENTO_PRINCIPAL_APAC);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);

				FatTipoCaractItens tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				FatCaractItemProcHosp ciph = new FatCaractItemProcHosp();
				ciph.setId(id);
				ciph.setTipoCaracteristicaItem(tipoCaractItem);
				ciph.setValorChar(newIph.getProcPrincipalApac() != null ? (newIph.getProcPrincipalApac() ? "S" : "N") : null);
				fatCaractItemProcHospDAO.persistir(ciph);
			}

			if(!Boolean.FALSE.equals(newIph.getCobrancaAmbulatorio())){
				Integer vTctSeq = tipoCaracteristicaItemRN.obterTipoCaractItemSeq(DominioFatTipoCaractItem.COBRA_BPA);
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(newIph.getId().getPhoSeq());
				id.setIphSeq(newIph.getId().getSeq());
				id.setTctSeq(vTctSeq);
				
				FatTipoCaractItens tipoCaractItem = getFatTipoCaractItensDAO().obterPorChavePrimaria(vTctSeq);
				FatCaractItemProcHosp ciph = new FatCaractItemProcHosp();
				ciph.setId(id);
				ciph.setTipoCaracteristicaItem(tipoCaractItem);
				ciph.setValorChar(newIph.getCobrancaAmbulatorio() != null ? (newIph.getCobrancaAmbulatorio() ? "S" : "N") : null);
				fatCaractItemProcHospDAO.persistir(ciph);
			}
		}
	}

	
	
	
	
	/** ORADB: FATT_IPH_BRI ON FAT_ITENS_PROCED_HOSPITALAR
	 * 
	 * @param itemProcedHospitalar
	 */
	public void executarAntesDeInserirItemProcedHospitalar(FatItensProcedHospitalar itemProcedHospitalar){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
	  /* VALORA IND_HCPA_CADASTRADO E IND_TIPO_AIH5 */
		if(itemProcedHospitalar.getHcpaCadastrado() == null){
			itemProcedHospitalar.setHcpaCadastrado(true);
		}
		if(itemProcedHospitalar.getTipoAih5() == null){
			itemProcedHospitalar.setTipoAih5(false);
		}
		
		itemProcedHospitalar.setCriadoEm(new Date());
		itemProcedHospitalar.setAlteradoEm(new Date());
		itemProcedHospitalar.setCriadoPor(servidorLogado.getUsuario());
		itemProcedHospitalar.setAlteradoPor(servidorLogado.getUsuario());
	}

	/** ORADB: FATT_IPH_BRU ON FAT_ITENS_PROCED_HOSPITALAR
	 * 
	 * @param itemProcedHospitalar
	 */
	public void executarAntesDeAtualizarItemProcedHospitalar(FatItensProcedHospitalar itemProcedHospitalar){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		itemProcedHospitalar.setAlteradoEm(new Date());
		itemProcedHospitalar.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
		
	}
	
	/** ORADB Function FATC_VER_COMPAT_ITEM
	 * 
	 * @param phoSeqCont
	 * @param iphSeqCont
	 * @param phoSeqComp
	 * @param iphSeqComp
	 * @return
	 */
	public String verFatCompatItem(Short phoSeqCont, Integer iphSeqCont, Short phoSeqComp, Integer iphSeqComp) {
		FatCompatExclusItemDAO dao = getFatCompatExclusItemDAO();

		List<FatCompatExclusItem> listaCompatExclusItem = dao.executarCursorCompatR(phoSeqCont, iphSeqCont, phoSeqComp, iphSeqComp, DominioIndComparacao.R,
				DominioIndCompatExclus.ICP, DominioSituacao.A);
		
		for (FatCompatExclusItem compatExclusItem : listaCompatExclusItem) {
			String comparacao = compatExclusItem.getItemProcedHospCompatibiliza().getId().getPhoSeq().toString() + compatExclusItem.getItemProcedHospCompatibiliza().getId().getSeq().toString(); 
			if (!comparacao.equals(phoSeqComp.toString() + iphSeqComp.toString())) {
				FatCompatExclusItem fatCompatExclusItem = dao.executarCursorCompatI(phoSeqComp, iphSeqComp, compatExclusItem.getItemProcedHospCompatibiliza().getId().getPhoSeq(), 
						compatExclusItem.getItemProcedHospCompatibiliza().getId().getSeq(), DominioIndComparacao.I);
				if (fatCompatExclusItem != null) {
					return fatCompatExclusItem.getIndCompatExclus().toString();
				}
			}
		}
		
		return "N";
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected FatItensProcedHospitalarJnDAO getFatItensProcedHospitalarJnDAO() {
		return fatItensProcedHospitalarJnDAO;
	}

	protected FatCaractItemProcHospDAO getFatCaractItemProcHospDAO() {
		return fatCaractItemProcHospDAO;
	}
	
	protected TipoCaracteristicaItemRN getTipoCaracteristicaItemRN() {
		return tipoCaracteristicaItemRN;
	}
	
	protected FatTipoCaractItensDAO getFatTipoCaractItensDAO() {
		return fatTipoCaractItensDAO;
	}
	
	protected FatCompatExclusItemDAO getFatCompatExclusItemDAO() {
		return fatCompatExclusItemDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
