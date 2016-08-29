package br.gov.mec.aghu.compras.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacaFatura;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.SceRmrPacienteVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatLogInterface;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceFornecedorMaterial;
import br.gov.mec.aghu.model.SceFornecedorMaterialId;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceItemRmpsId;
import br.gov.mec.aghu.model.SceRefCodes;
import br.gov.mec.aghu.model.SceReqMaterialRetornos;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.model.VFatContasHospPacientes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
@Stateless
public class ManterItemRmpsRN extends BaseBusiness {

@EJB
private ScekIpsRN scekIpsRN;
@EJB
private ManterFornecedorMaterialON manterFornecedorMaterialON;

	private static final Log LOG = LogFactory.getLog(ManterItemRmpsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 9064680014137274730L;

	public enum ManterItemRmpsRNExceptionCode implements BusinessExceptionCode {
		SCE_00149, SCE_00587, SCE_00588, SCE_00589, SCE_00956, SCE_00299, SCE_00832, SCE_00833, SCE_00949, SCE_00957, MSG_ITEM_SITUACAO_NAO_PERMITE_TROCA_MATERIAL, MSG_CONTA_PACIENTE_ENCERRADA, FAT_00017, FAT_00198, FAT_00163, FAT_00602;
	}

	/**
	 * ORADB Trigger SCET_IPS_BRI
	 * 
	 * @param itemRmps
	 * @throws BaseException
	 */
	public void executarAntesInserirItemRmps(final SceItemRmps itemRmps) throws BaseException {

		// verifica fornecedor do item IRR = ao fornecedor da RMP
		final Integer ealSeq = itemRmps.getEstoqueAlmoxarifado() == null ? null : itemRmps.getEstoqueAlmoxarifado().getSeq();
		if (itemRmps.getItemRmr() != null) {
			rnIpspVerFornIrr(itemRmps.getItemRmr().getId().getRmrSeq(), itemRmps.getItemRmr().getId().getEalSeq());
		} else {
			rnIpspVerFornEal(itemRmps.getId().getRmpSeq(), ealSeq);
		}

		// verifica data
		rnIpspVerData(itemRmps.getData());

		// verifica se item RMR está efetivado
		if (itemRmps.getItemRmr() != null) {
			rnIpspVerRmrEfet(itemRmps.getItemRmr().getId().getRmrSeq());
		}

		// verifica se tem RMP com nota informada
		if (itemRmps.getNotaFiscal() != null) {
			verIpspNotaFiscal(itemRmps.getId().getRmpSeq());
		}

		// grava ou atualiza anvisa reg e cnpj na tab sce_fornecedor_material
		// RN_IPSP_ATU_MAT_CRG -> não migrar segundo Hoffmann
		if (DominioSituacaFatura.G.equals(itemRmps.getSituacaoFatura())) {
			// itemRmps.getItensContasHospitalar().iterator().next().getProcedimentoHospitalarInterno()
			// é uma solução temporária devido a innexistência do
			// estoque/almoxarifado para busca do PHI
			rnIpspAtuFmt(itemRmps.getRegistroAnvisa(), itemRmps.getCnpjRegistroAnvisa(), itemRmps.getId().getRmpSeq(), ealSeq, itemRmps
					.getItensContasHospitalar().iterator().next().getProcedimentoHospitalarInterno());
		}

		if (itemRmps.getSituacaoFatura() != null) {
			if (!scecCheckReferenceCode(itemRmps.getSituacaoFatura().toString())) {
				throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00956);
			}
		}
	}

	/**
	 * ORADB Trigger SCET_IPS_BRU
	 * 
	 * @param oldItemRmps
	 * @param newItemRmps
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void executarAntesAtualizarItemRmps(final SceItemRmps oldItemRmps, final SceItemRmps newItemRmps) throws BaseException {
		// -- verifica fornecedor do item IRR = ao fornecedor da RMP
		if (newItemRmps.getItemRmr() != null && oldItemRmps.getItemRmr() != null
				&& CoreUtil.modificados(newItemRmps.getItemRmr().getId().getEalSeq(), oldItemRmps.getItemRmr().getId().getEalSeq())) {
			rnIpspVerFornIrr(newItemRmps.getId().getRmpSeq(), newItemRmps.getItemRmr().getId().getEalSeq());
		}
		
		// -- verifica fornecedor do item = ao fornecedor da RMP
		if (newItemRmps.getEstoqueAlmoxarifado() != null && oldItemRmps.getEstoqueAlmoxarifado() != null
				&& CoreUtil.modificados(newItemRmps.getEstoqueAlmoxarifado().getSeq(), oldItemRmps.getEstoqueAlmoxarifado().getSeq())) {
			rnIpspVerFornEal(newItemRmps.getId().getRmpSeq(), newItemRmps.getEstoqueAlmoxarifado().getSeq());
		}

		// -- verifica data
		if (newItemRmps.getData() != null && oldItemRmps.getData() != null && !newItemRmps.getData().equals(oldItemRmps.getData())) {
			rnIpspVerData(newItemRmps.getData());
		}

		// -- verifica se item RMR está efetivado
		if (newItemRmps.getItemRmr() != null && oldItemRmps.getItemRmr() != null
				&& CoreUtil.modificados(newItemRmps.getItemRmr().getId().getRmrSeq(), oldItemRmps.getItemRmr().getId().getRmrSeq())) {
			rnIpspVerRmrEfet(newItemRmps.getItemRmr().getId().getRmrSeq());
		}

		// -- verifica se tem RMP com nota informada
		if (CoreUtil.modificados(newItemRmps.getNotaFiscal(), oldItemRmps.getNotaFiscal())) {
			verIpspNotaFiscal(newItemRmps.getId().getRmpSeq());
		}

		if (CoreUtil.modificados(newItemRmps.getSituacaoFatura(), oldItemRmps.getSituacaoFatura())
				&& DominioSituacaFatura.G.equals(newItemRmps.getSituacaoFatura())) {
			// itemRmps.getItensContasHospitalar().iterator().next().getProcedimentoHospitalarInterno()
			// é uma solução temporária devido a innexistência do
			// estoque/almoxarifado para busca do PHI
			rnIpspAtuFmt(newItemRmps.getRegistroAnvisa(), newItemRmps.getCnpjRegistroAnvisa(), newItemRmps.getId().getRmpSeq(),
					newItemRmps.getEstoqueAlmoxarifado() == null ? null : newItemRmps.getEstoqueAlmoxarifado().getSeq(), newItemRmps
							.getItensContasHospitalar().iterator().next().getProcedimentoHospitalarInterno());
		}
		
		if (newItemRmps.getSituacaoFatura() != null
				&& (oldItemRmps.getSituacaoFatura() == null || !newItemRmps.getSituacaoFatura().equals(oldItemRmps.getSituacaoFatura()))) {
			if (!scecCheckReferenceCode(newItemRmps.getSituacaoFatura().toString())) {
				throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00957);
			}
		}

		if (newItemRmps.getEstoqueAlmoxarifado() != null && oldItemRmps.getEstoqueAlmoxarifado() != null
				&& CoreUtil.modificados(newItemRmps.getEstoqueAlmoxarifado().getSeq(), oldItemRmps.getEstoqueAlmoxarifado().getSeq())
				&& !DominioSituacaFatura.G.equals(newItemRmps.getSituacaoFatura()) && !DominioSituacaFatura.P.equals(newItemRmps.getSituacaoFatura())
				&& !DominioSituacaFatura.C.equals(newItemRmps.getSituacaoFatura())) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.MSG_ITEM_SITUACAO_NAO_PERMITE_TROCA_MATERIAL);
		}
		
		// -- Ao ser Liberada a Fatura, é gerada a Parcela de Programação base
		// da Fatura.
		if (CoreUtil.modificados(newItemRmps.getSituacaoFatura(), oldItemRmps.getSituacaoFatura())
				&& DominioSituacaFatura.G.equals(oldItemRmps.getSituacaoFatura()) && DominioSituacaFatura.P.equals(oldItemRmps.getSituacaoFatura())
				&& DominioSituacaFatura.C.equals(oldItemRmps.getSituacaoFatura()) && DominioSituacaFatura.L.equals(newItemRmps.getSituacaoFatura())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			newItemRmps.setDataLiberacao(new Date());
			newItemRmps.setServidor(servidorLogado);

			// -- Gera Programação de Entrega para Fatura do Material
			// -- SubstituI a procedure RN_IPSP_GER_PROG_ENT (Versão anterior)
			// em função da implantação do Projeto Livros Fiscais (24/05/10,
			// Hoffmann).
			// SCEK_IPS_RN.RN_IPSP_GER_PROGENTG (:new.rmp_seq, :new.numero,
			// :new.eal_seq, :new.quantidade);
			// OBS.: Não migrar segundo Hoffmann
		}

		// -- Em caso de Estorno da Dispensa de Mat de Órtese / Prótese, os
		// Itens das respectivas CUM são Excluídos.
		// -- Nesse caso, os mesmos também devem ser excluídos dos Itens da
		// Conta Hospitalar.
		if (CoreUtil.modificados(newItemRmps.getSituacaoFatura(), oldItemRmps.getSituacaoFatura())
				&& DominioSituacaFatura.X.equals(newItemRmps.getSituacaoFatura())) {
			// -- Verifica se o Item da CUM existe nos Itens de Faturamento
			final List<FatItemContaHospitalar> itemsContaHosp = getFaturamentoFacade().obterItensContaHospitalarPorItemRmps(
					newItemRmps.getId().getRmpSeq(), newItemRmps.getId().getNumero());
			for (final FatItemContaHospitalar itemContaHosp : itemsContaHosp) {
				if (itemContaHosp != null) {
					// -- Verifica a Conta do Paciente. Não deve estar Encerrada
					final VFatContaHospitalarPac contaHospPac = getFaturamentoFacade().buscarPrimeiraContaHospitalarPaciente(
							itemContaHosp.getId().getCthSeq());
					if (contaHospPac != null) {
						if (DominioSituacaFatura.A.equals(contaHospPac.getIndSituacao())
								|| DominioSituacaFatura.F.equals(contaHospPac.getIndSituacao())) {
							// -- Exclui Item Faturamento
							getFaturamentoFacade().removerItemContaHospitalar(itemContaHosp);
						}
					} else {
						throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.MSG_CONTA_PACIENTE_ENCERRADA);
					}
				}
			}
		}

	}

	/**
	 * ORADB Procedure RN_IPSP_VER_FORN_IRR
	 * 
	 * @param newRmpSeq
	 * @param newEalSeq
	 * @throws ApplicationBusinessException
	 */
	public void rnIpspVerFornIrr(final Integer newRmpSeq, final Integer newIrrEalSeq) throws ApplicationBusinessException {
		IEstoqueFacade estoqueFacade = this.getEstoqueFacade();

		final SceRmrPaciente paciente = estoqueFacade.obterSceRmrPacientePorChavePrimaria(newRmpSeq);
		if (paciente != null && paciente.getScoFornecedor() != null) {
			if (estoqueFacade.listaEstoqueAlmoxarifado(paciente.getScoFornecedor().getNumero(), newIrrEalSeq).isEmpty()) {
				throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00589);
			}
		}
	}

	/**
	 * ORADB Procedure RN_IPSP_VER_FORN_EAL
	 * 
	 * @param newRmpSeq
	 * @param newEalSeq
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void rnIpspVerFornEal(final Integer newRmpSeq, final Integer newEalSeq) throws ApplicationBusinessException {
		SceEstoqueAlmoxarifado estoqueAlmoxarifado = null;
		IEstoqueFacade estoqueFacade = this.getEstoqueFacade();

		// if (newRmpSeq == null || newEalSeq == null)
		// throw new
		// ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00589);

		if (newEalSeq != null) {
			estoqueAlmoxarifado = estoqueFacade.obterSceEstoqueAlmoxarifadoPorChavePrimaria(newEalSeq);
		}
		
		if (estoqueAlmoxarifado != null
				&& estoqueAlmoxarifado.getFornecedor() != null
				&& !estoqueAlmoxarifado.getFornecedor().getNumero()
						.equals(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NUMERO_FORNECEDOR_HU).getVlrNumerico().intValue())) {
			if (estoqueFacade.listaEstoqueAlmoxarifado(newRmpSeq, newEalSeq).isEmpty()) {
				throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00589);
			}
		}
	}

	/**
	 * ORADB Procedure RN_IPSP_VER_DATA
	 * 
	 * @param newData
	 * @throws ApplicationBusinessException
	 */
	public void rnIpspVerData(final Date newData) throws ApplicationBusinessException {
		if (newData != null && newData.after(new Date())) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00588);
		}
	}

	/**
	 * ORADB Function SCEC_CHECK_REFERENCE_CODE
	 * 
	 * @param valor
	 * @param dominio
	 * @return
	 */
	public Boolean scecCheckReferenceCode(final String valor, final String dominio) {
		final List<SceRefCodes> listaRefCodes = getEstoqueFacade().buscarSceRefCodesPorTipoOperConversao(valor, dominio);
		return !listaRefCodes.isEmpty();
	}

	/**
	* ORADB Function SCEC_CHECK_REFERENCE_CODE (REMOVIDO FUNCTION)
	* Utilizado DominioSituacaFatura para fazer validação.
	* 
	* @param valor
	* @param dominio
	* @return
	*/
	public Boolean scecCheckReferenceCode(final String valor) {
		final DominioSituacaFatura[] itensSituacao = DominioSituacaFatura.values();
		for (DominioSituacaFatura dominioSituacaFatura : itensSituacao) {
			if (dominioSituacaFatura.toString() == valor) {
				return true;
			}
		}
		return false;
	}

	
	
	
	/**
	 * ORADB Procedure RN_IPSP_VER_RMR_EFET
	 * 
	 * @param newData
	 * @throws ApplicationBusinessException
	 */
	public void rnIpspVerRmrEfet(final Integer newIrrRmrSeq) throws ApplicationBusinessException {
		SceReqMaterialRetornos reqMaterialRetorno = null;
		if (newIrrRmrSeq != null) {
			reqMaterialRetorno = getEstoqueFacade().obterSceReqMaterialRetornosPorChavePrimaria(newIrrRmrSeq);
		}
		
		if (reqMaterialRetorno == null) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00149);
		}
		
		if (reqMaterialRetorno.getIndSituacao().equals("E") || reqMaterialRetorno.getIndSituacao().equals("A")) {
			// TODO refatorar quando criar o domínio no POJO
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00587);
		}
	}

	/**
	 * ORADB SCEP_ENFORCE_IPS_RULES
	 * 
	 * @param itemNew
	 * @param itemOld
	 * @param operacao
	 * @throws BaseException
	 */

	protected void enforce(final SceItemRmps itemNew, final SceItemRmps itemOld, final DominioOperacaoBanco operacao, final Date dataFimVinculoServidor) throws BaseException {

		Short convSus = null;
		Short convenio = null;

		if (DominioOperacaoBanco.DEL.equals(operacao)) {
			getScekIpsRN().ipspVerUltItem(itemOld.getId().getRmpSeq());
			
		} else if (DominioOperacaoBanco.UPD.equals(operacao) || DominioOperacaoBanco.INS.equals(operacao)) {
			final AghParametros paramConvPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONV_PADRAO);
			convSus = paramConvPadrao.getVlrNumerico().shortValue();

			final SceRmrPaciente rmrPac = this.getEstoqueFacade().obterSceRmrPacientePorChavePrimaria(itemNew.getId().getRmpSeq());
			if (rmrPac != null) {
				if (rmrPac.getInternacao() != null) {

					convenio = rmrPac.getInternacao().getConvenioSaude() != null ? rmrPac.getInternacao().getConvenioSaude().getCodigo() : null;

				} else if (rmrPac.getCirurgia() != null) {

					convenio = rmrPac.getCirurgia().getConvenioSaude() != null ? rmrPac.getCirurgia().getConvenioSaude().getCodigo() : null;
				}
			}
			if (convSus.equals(convenio)) {
				// itemRmps.getItensContasHospitalar().iterator().next().getProcedimentoHospitalarInterno()
				// é uma solução temporária devido a innexistência do
				// estoque/almoxarifado para busca do PHI
				final Integer phiSeq = itemNew.getItensContasHospitalar().iterator().next().getProcedimentoHospitalarInterno().getSeq();
				this.fatpAtuIchSce(itemNew.getId().getRmpSeq(), itemNew.getId().getNumero(), itemNew.getQuantidade(), phiSeq, dataFimVinculoServidor);
			}

		}
	}

	/**
	 * ORADB FATP_ATU_ICH_SCE TODO: Procedure alterada pois não existe dados de
	 * almoxarifado(sce_estq_almoxs) Deverá ser corrigida a assinatura do metodo
	 * e a consulta interna quando for utilizada essa tabela
	 * 
	 * @param rmpSeq
	 * @param numero
	 * @param quantidade
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	protected void fatpAtuIchSce(final Integer rmpSeq, final Short numero, final Integer quantidade, final Integer phiSeqICH, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		//final IAghuFacade aghuFacade = this.getAghuFacade();
		final IParametroFacade parametroFacade = this.getParametroFacade();

		final List<SceRmrPacienteVO> listaRmrPac = this.getEstoqueFacade().listarRmrPacientePorRmpSeqENumero(rmpSeq, numero);
		if (listaRmrPac == null || listaRmrPac.isEmpty() || phiSeqICH == null) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.FAT_00017);
		}

		final SceRmrPacienteVO rmrPacVO = listaRmrPac.get(0);
		rmrPacVO.setPhiSeq(phiSeqICH);

		final StringBuilder mensagem = new StringBuilder(35);

		Short cnvCodigo = null;// codigo do convenio.
		Byte cspSeq = null;// seq do plano.
		Short unfSeq = null;// seq da unidade funcional.
		Integer pacCodigo = null;// codigo do paciente.

		MbcCirurgias cirurgia = null;
		if (rmrPacVO.getCrgSeq() != null) {
			cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(rmrPacVO.getCrgSeq());
			cnvCodigo = cirurgia.getConvenioSaudePlano().getId().getCnvCodigo();
			// conforme mapeamento, convenioSaudePlano eh not null.
			cspSeq = cirurgia.getConvenioSaudePlano().getId().getSeq();
			// conforme mapeamento, paciente eh not null.
			pacCodigo = cirurgia.getPaciente().getCodigo();

			unfSeq = cirurgia.getSalaCirurgica() != null ? cirurgia.getSalaCirurgica().getUnidadeFuncional().getSeq() : null;// conforme
		}

		mensagem.append("CRG=").append(rmrPacVO.getCrgSeq());

		AinInternacao internacao = null;
		if (pacCodigo == null && rmrPacVO.getIntSeq() != null) {
			internacao = getInternacaoFacade().obterInternacao(rmrPacVO.getIntSeq());
			cnvCodigo = internacao.getConvenioSaudePlano().getId().getCnvCodigo();
			// conforme mapeamento, convenioSaudePlano eh not null.
			cspSeq = internacao.getConvenioSaudePlano().getId().getSeq();
			// conforme mapeamento, paciente eh not null.
			pacCodigo = internacao.getPaciente().getCodigo();
		}

		if (pacCodigo == null) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.FAT_00198);
		}

		if (cnvCodigo != null) {
			final FatConvenioSaude convenioSaude = faturamentoFacade.obterFatConvenioSaudePorId(cnvCodigo);
			if (convenioSaude == null || !DominioGrupoConvenio.S.equals(convenioSaude.getGrupoConvenio())) {
				return;// Apenas termina a execucao, conforme a rotina original.
			}
		} else {
			return;// Apenas termina a execucao, conforme a rotina original.
		}

		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (cirurgia != null) {
			mensagem.append("CRG=").append(sdf.format(cirurgia.getDataFimCirurgia()));
		} else {
			mensagem.append("CRG=");
		}
		if (rmrPacVO.getDtUtilizacao() != null) {
			mensagem.append(" UTL=").append(sdf.format(rmrPacVO.getDtUtilizacao()));
		} else {
			mensagem.append(" UTL=");
		}
		if (internacao != null) {
			mensagem.append(" INT=").append(sdf.format(internacao.getDthrInternacao()));
		} else {
			mensagem.append(" INT=");
		}
		mensagem.append(' ');

		Date dthrRealizado = null;
		if (cirurgia != null && cirurgia.getDataFimCirurgia() != null) {
			dthrRealizado = cirurgia.getDataFimCirurgia();
		} else if (rmrPacVO.getDtUtilizacao() != null) {
			dthrRealizado = rmrPacVO.getDtUtilizacao();
		} else if (internacao != null && internacao.getDthrInternacao() != null) {
			dthrRealizado = internacao.getDthrInternacao();
		} else {
			dthrRealizado = new Date();
		}

		final DominioSituacaoConta dominiosSitCth[] = { DominioSituacaoConta.A, DominioSituacaoConta.F, DominioSituacaoConta.E };
		final List<VFatContasHospPacientes> listaVFatContasHospPaciente = faturamentoFacade.listarContasPorPacCodigoDthrRealizadoESituacaoCth(
				pacCodigo, dthrRealizado, dominiosSitCth, VFatContasHospPacientes.Fields.CTH_DT_INT_ADMINISTRATIVA.toString(), false);
		if (listaVFatContasHospPaciente == null || listaVFatContasHospPaciente.isEmpty()) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.FAT_00163);
		}

		final VFatContasHospPacientes vFatContasHospPacientes = listaVFatContasHospPaciente.get(0);
		final FatConvenioSaudePlano convSaudePlano = faturamentoFacade.obterFatConvenioSaudePlano(cnvCodigo, cspSeq);
		if (convSaudePlano != null && DominioTipoPlano.A.equals(convSaudePlano.getIndTipoPlano())) {
			Integer ssm = null;

			if (vFatContasHospPacientes.getCthPhiSeqRealizado() != null) {
				ssm = vFatContasHospPacientes.getCthPhiSeqRealizado();
			} else {
				ssm = vFatContasHospPacientes.getCthPhiSeq();
			}

			final AghParametros pSusPlanoInternacao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
			final AghParametros pTipoCaracItemCobraEmAih = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_CARAC_ITEM_COBRA_EM_AIH);

			final FatTipoCaractItens tipoCaractItem = faturamentoFacade.obterTipoCaracteristicaItemPorChavePrimaria(pTipoCaracItemCobraEmAih
					.getVlrNumerico().intValue());
			if (faturamentoFacade.fatcVerCaractPhiQrInt(cnvCodigo, pSusPlanoInternacao.getVlrNumerico().byteValue(), ssm,
					tipoCaractItem.getCaracteristica()) != true) {
				return;// Apenas termina a execucao, conforme a rotina original.
			}
		}

		if (!DominioSituacaoConta.A.equals(vFatContasHospPacientes.getCthIndSituacao())
				&& !DominioSituacaoConta.F.equals(vFatContasHospPacientes.getCthIndSituacao())) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.FAT_00602);
		}

		// busca Unidade Funcional paciente da cirurgia
		if (unfSeq == null) {
			if (cirurgia != null) {
				unfSeq = cirurgia.getUnidadeFuncional().getSeq();
			} else if (internacao != null && internacao.getSeq() != null) {
				// TODO: procurar por criado_em ou por lancamento?!
				final List<AinMovimentosInternacao> listaMovInternacao = this.getInternacaoFacade().listarMotivosInternacaoPorInternacaoECriadoem(
						internacao.getSeq(), dthrRealizado, AinMovimentosInternacao.Fields.CRIADO_EM.toString(), false);
				if (listaMovInternacao != null && !listaMovInternacao.isEmpty()) {
					final AinMovimentosInternacao mov = listaMovInternacao.get(0);
					unfSeq = mov.getUnidadeFuncional() != null ? mov.getUnidadeFuncional().getSeq() : null;
				}
			}
		}

		AghUnidadesFuncionais unidadeFuncional = null;
		if (unfSeq != null) {
			unidadeFuncional = null;//aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
		}

		List<FatItemContaHospitalar> itens = faturamentoFacade.obterItensContaHospitalarPorItemRmps(rmpSeq, numero);
		if (itens == null || itens.isEmpty()) {
			final FatItemContaHospitalar item = faturamentoFacade.obterItensContaHospitalarPorContaHospitalarePHI(vFatContasHospPacientes.getCthSeq(),
					rmrPacVO.getPhiSeq());
			if (item != null) {
				itens = new ArrayList<FatItemContaHospitalar>(1);
				itens.add(item);
			}
		}

		final FatItemContaHospitalarId itemContaId = new FatItemContaHospitalarId();
		itemContaId.setCthSeq(vFatContasHospPacientes.getCthSeq());

		if (itens != null && !itens.isEmpty()) {
			for (final FatItemContaHospitalar item : itens) {
				item.setQuantidadeRealizada(quantidade != null ? quantidade.shortValue() : null);

				if (item.getUnidadesFuncional() == null) {
					item.setUnidadesFuncional(unidadeFuncional);
				}

				if (item.getItemRmps() == null) {
					final SceItemRmpsId id = new SceItemRmpsId(rmpSeq, numero);
					final SceItemRmps itemRmps = getEstoqueFacade().obterSceItemRmpsPorChavePrimaria(id);
					item.setItemRmps(itemRmps);
				}
				faturamentoFacade.persistirItemContaHospitalar(item, null, false, servidorLogado, dataFimVinculoServidor);
			}
			mensagem.append("UPD QTD ICH ");
		} else {
			mensagem.append("INS ICH ");

			final FatItemContaHospitalar itemConta = new FatItemContaHospitalar();
			itemConta.setId(itemContaId);
			itemConta.setIchType(DominioItemConsultoriaSumarios.ICH);

			final FatProcedHospInternos phi = faturamentoFacade.obterProcedimentoHospitalarInterno(rmrPacVO.getPhiSeq());

			itemConta.setProcedimentoHospitalarInterno(phi);
			itemConta.setIndSituacao(DominioSituacaoItenConta.A);
			itemConta.setQuantidadeRealizada(quantidade != null ? quantidade.shortValue() : null);
			itemConta.setIndOrigem(DominioIndOrigemItemContaHospitalar.BCC);
			itemConta.setLocalCobranca(DominioLocalCobranca.I);
			itemConta.setDthrRealizado(dthrRealizado);

			if (unfSeq != null) {
				unidadeFuncional = null;//aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
				itemConta.setUnidadesFuncional(unidadeFuncional);
			}

			final SceItemRmpsId id = new SceItemRmpsId(rmpSeq, numero);
			final SceItemRmps itemRmps = getEstoqueFacade().obterSceItemRmpsPorChavePrimaria(id);
			itemConta.setItemRmps(itemRmps);

			faturamentoFacade.persistirItemContaHospitalar(itemConta, null, false, servidorLogado, dataFimVinculoServidor);
		}

		Boolean encontrouPhiSeq = false;

		String listaPhis = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PHIS_ITENS_RMPS_PARA_PERFUSIONISTA).getVlrTexto();
		String[] arrayPhisString = listaPhis.split(",");
		// Integer arrayPhis1 [] = {14819, 14820,11948,12162,12164,12165,
		// 12166,12169,12231,12232,14818,15412,15413};
		for (final String phiSeq : arrayPhisString) {
			if (rmrPacVO.getPhiSeq().equals(Integer.valueOf(phiSeq.trim()))) {
				encontrouPhiSeq = true;
				break;
			}
		}

		if (encontrouPhiSeq) {
			mensagem.append("INS dec ");
			persisteItemContaHospitalar(quantidade, dthrRealizado, vFatContasHospPacientes.getCthSeq(), unidadeFuncional, itemContaId,
					AghuParametrosEnum.P_PHI_SEQ_PERFUSIONISTA, dataFimVinculoServidor);
		} else {
			encontrouPhiSeq = false;
			listaPhis = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PHIS_ITENS_RMPS_PARA_CATETER_MONO_LUMEN).getVlrTexto();
			arrayPhisString = listaPhis.split(",");
			// Integer arrayPhis2 [] = {19519,19520,19521};
			for (final String phiSeq : arrayPhisString) {
				if (rmrPacVO.getPhiSeq().equals(Integer.valueOf(phiSeq.trim()))) {
					encontrouPhiSeq = true;
					break;
				}
			}

			if (encontrouPhiSeq) {
				mensagem.append("INS dec ");
				persisteItemContaHospitalar(quantidade, dthrRealizado, vFatContasHospPacientes.getCthSeq(), unidadeFuncional, itemContaId,
						AghuParametrosEnum.P_PHI_SEQ_INSTALACAO_CATETER_MONO_LUMEN, dataFimVinculoServidor);

			} else {
				encontrouPhiSeq = false;
				listaPhis = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PHIS_ITENS_RMPS_PARA_CATETER_LONG_PERM).getVlrTexto();
				arrayPhisString = listaPhis.split(",");
				// Integer arrayPhis3 [] =
				// {11907,11954,12155,13593,13595,13980,15766,15824,15960,16046,16054,16411,27563,30639};
				for (final String phiSeq : arrayPhisString) {
					if (rmrPacVO.getPhiSeq().equals(Integer.valueOf(phiSeq.trim()))) {
						encontrouPhiSeq = true;
						break;
					}
				}

				if (encontrouPhiSeq) {
					mensagem.append("INS dec ");
					persisteItemContaHospitalar(quantidade, dthrRealizado, vFatContasHospPacientes.getCthSeq(), unidadeFuncional, itemContaId,
							AghuParametrosEnum.P_PHI_SEQ_IMPLANTACAO_CATETER_LONG_PERM, dataFimVinculoServidor);
				}
			}

			final FatLogInterface logInterface = new FatLogInterface();

			logInterface.setSistema(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_ESTOQUE).getVlrTexto());
			logInterface.setDthrChamada(new Date());
			logInterface.setPacCodigo(cirurgia != null ? cirurgia.getPaciente().getCodigo().longValue() : null);
			logInterface.setSceRmpSeq(rmpSeq);
			logInterface.setSceNumero(numero);
			logInterface.setSceQuantidade(quantidade);
			logInterface.setMensagem(mensagem.toString());
			logInterface.setLinProcedure("FATP_ATU_ICH_SCE");

			faturamentoFacade.inserirFatLogInterface(logInterface);

		}

	}

	private void persisteItemContaHospitalar(final Integer quantidade, final Date dthrRealizado, final Integer cthSeq,
			final AghUnidadesFuncionais unidadeFuncional, final FatItemContaHospitalarId itemContaId, final AghuParametrosEnum parametro, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException, BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		final IParametroFacade parametroFacade = this.getParametroFacade();

		final int phiSeqParam = parametroFacade.buscarAghParametro(parametro).getVlrNumerico().intValue();
		FatItemContaHospitalar itemConta = faturamentoFacade.obterItensContaHospitalarPorContaHospitalarePHI(cthSeq, Integer.valueOf(phiSeqParam));

		if (itemConta == null) {
			itemConta = createItemContaHospitalar(quantidade, dthrRealizado, unidadeFuncional, itemContaId, phiSeqParam);
			faturamentoFacade.persistirItemContaHospitalar(itemConta, null, false, servidorLogado, dataFimVinculoServidor);
		}

	}

	private FatItemContaHospitalar createItemContaHospitalar(final Integer quantidade, final Date dthrRealizado,
			final AghUnidadesFuncionais unidadeFuncional, final FatItemContaHospitalarId itemContaId, final int phiSeqParam) {
		FatItemContaHospitalar itemConta;
		itemConta = new FatItemContaHospitalar();
		itemConta.setId(itemContaId);
		itemConta.setIchType(DominioItemConsultoriaSumarios.ICH);

		final FatProcedHospInternos phi = this.getFaturamentoFacade().obterProcedimentoHospitalarInterno(phiSeqParam);

		itemConta.setProcedimentoHospitalarInterno(phi);
		itemConta.setIndSituacao(DominioSituacaoItenConta.A);
		itemConta.setQuantidadeRealizada(quantidade != null ? quantidade.shortValue() : null);
		itemConta.setIndOrigem(DominioIndOrigemItemContaHospitalar.BCC);
		itemConta.setLocalCobranca(DominioLocalCobranca.I);
		itemConta.setDthrRealizado(dthrRealizado);

		itemConta.setUnidadesFuncional(unidadeFuncional);
		return itemConta;
	}

	/**
	 * ORADB SCET_IPS_ASI
	 * 
	 * @param itemNew
	 * @param operacao
	 */
	public void executarStatementAposInserir(final SceItemRmps itemNew, final Date dataFimVinculoServidor) throws BaseException {
		this.enforce(itemNew, null, DominioOperacaoBanco.INS, dataFimVinculoServidor);
	}

	/**
	 * ORADB SCET_IPS_ASU
	 * 
	 * @param itemNew
	 * @param oldNew
	 * @param operacao
	 */
	public void executarStatementAposAtualizar(final SceItemRmps itemNew, final SceItemRmps oldNew, final Date dataFimVinculoServidor) throws BaseException {
		this.enforce(itemNew, oldNew, DominioOperacaoBanco.UPD, dataFimVinculoServidor);
	}

	/**
	 * ORADB : TRIGGER SCET_RMP_BRI
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void preInserir(final SceRmrPaciente paciente) throws BaseException {
		this.atualizaServidor(paciente);
		// unreachable code
		if (paciente.getNfGeral() != null) {
			this.verNotaFiscal(paciente.getSeq());
		}
	}

	/**
	 * ORADB : TRIGGER SCET_RMP_BRU
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void preAtualizar(final SceRmrPaciente paciente, final SceRmrPaciente pacienteOld) throws BaseException {
		if (paciente != null && pacienteOld != null) {
			if (CoreUtil.modificados(paciente.getNfGeral(), pacienteOld.getNfGeral())) {
				// verifica se tem ITEM RMP com nota informada
				this.verNotaFiscal(paciente.getSeq());
			}
		}
	}

	/**
	 * ORADB : SCEK_RMP_RN.RN_RMPP_ATU_SERVIDOR e
	 * SCEK_SCE_RN.RN_SCEP_ATU_SERVIDOR
	 * 
	 * @param sceRmrPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void atualizaServidor(final SceRmrPaciente paciente) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00299);
		}
		paciente.setServidor(servidorLogado);
		paciente.setDtGeracao(new Date());
	}

	/**
	 * ORADB : SCEK_RMP_RN.RN_RMPP_VER_NF
	 * 
	 * @param fornecedorMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void verNotaFiscal(final Integer rmpSeq) throws BaseException {
		final List<SceItemRmps> lista = getEstoqueFacade().listarItensRmpsPorRmpSeqNfNaoNula(rmpSeq);
		if (!lista.isEmpty()) {
			// A Nota Fiscal foi informada para itens de RMP
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00832);
		}
	}

	/**
	 * ORADB : SCEK_IPS_RN.RN_IPSP_VER_NF
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void verIpspNotaFiscal(final Integer seq) throws BaseException {
		final List<SceRmrPaciente> lista = getEstoqueFacade().listarRmrPacientesPorSeqNfGeralNaoNula(seq);
		if (!lista.isEmpty()) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00833);
		}
	}

	/**
	 * ORADB Procedure SCEK_IPS_RN.RN_IPSP_ATU_FMT Nao temos almoxarifado logo
	 * traz o phi para pegar o material
	 * 
	 * @param regAnvisa
	 * @param cnpjRegAnvisa
	 * @param rmpSeq
	 * @param ealSeq
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void rnIpspAtuFmt(final String regAnvisa, final Long cnpjRegAnvisa, final Integer rmpSeq, final Integer ealSeq,
			final FatProcedHospInternos phi) throws BaseException {
		SceRmrPaciente rmrPaciente = null;
		if (rmpSeq != null) {
			rmrPaciente = getEstoqueFacade().obterSceRmrPacientePorChavePrimaria(rmpSeq);
		}
		
		if (rmrPaciente == null || rmrPaciente.getScoFornecedor() == null) {
			throw new ApplicationBusinessException(ManterItemRmpsRNExceptionCode.SCE_00949);
		}
		
		SceEstoqueAlmoxarifado estoqueAlmoxarifado = null;
		Integer materialCodigo = null;
		if (ealSeq != null) {
			estoqueAlmoxarifado = this.getEstoqueFacade().obterSceEstoqueAlmoxarifadoPorChavePrimaria(ealSeq);
			if (estoqueAlmoxarifado != null && estoqueAlmoxarifado.getMaterial() != null) {
				materialCodigo = estoqueAlmoxarifado.getMaterial().getCodigo();
			}
		}

		// Validacao desabilitada pois nao temos almoxarifado
		/*
		 * if (estoqueAlmoxarifado == null || estoqueAlmoxarifado.getMaterial()
		 * == null) throw new
		 * ApplicationBusinessException(ManterItemRmpsRNExceptionCode
		 * .SCE_00950);
		 */

		if (estoqueAlmoxarifado == null && phi != null) {
			if (phi != null && phi.getMaterial() != null) {
				materialCodigo = phi.getMaterial().getCodigo();
			}
		}

		SceFornecedorMaterial fornecMaterial = null;
		if (rmrPaciente.getScoFornecedor() != null && materialCodigo != null) {
			fornecMaterial = getEstoqueFacade().obterFornecedorMaterialPorFornecedorNumeroEMaterialCodigo(
					rmrPaciente.getScoFornecedor().getNumero(), materialCodigo);
		}

		if (fornecMaterial == null) {
			final SceFornecedorMaterial elemento = new SceFornecedorMaterial();
			elemento.setId(new SceFornecedorMaterialId(rmrPaciente.getScoFornecedor().getNumero(), materialCodigo));
			elemento.setRegistroAnvisa(regAnvisa);
			elemento.setCnpjRegistroAnvisa(cnpjRegAnvisa);

			getManterFornecedorMaterialON().persistirFornecedorMaterial(elemento, true);

		} else {
			if (fornecMaterial.getRegistroAnvisa() == null || !regAnvisa.equals(fornecMaterial.getRegistroAnvisa())
					|| fornecMaterial.getCnpjRegistroAnvisa() == null || !cnpjRegAnvisa.equals(fornecMaterial.getCnpjRegistroAnvisa())) {
				final SceFornecedorMaterial elemento = getEstoqueFacade().obterFornecedorMaterialPorFornecedorNumeroEMaterialCodigo(
						rmrPaciente.getScoFornecedor().getNumero(), materialCodigo);
				elemento.setRegistroAnvisa(regAnvisa);
				elemento.setCnpjRegistroAnvisa(cnpjRegAnvisa);

				getManterFornecedorMaterialON().persistirFornecedorMaterial(elemento, true);
			}
		}
	}
	
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ManterFornecedorMaterialON getManterFornecedorMaterialON() {
		return manterFornecedorMaterialON;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected ScekIpsRN getScekIpsRN() {
		return scekIpsRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
			
}
