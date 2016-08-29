package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioIndFotoSensibilidade;
import br.gov.mec.aghu.dominio.DominioIndUnidTempoMdto;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioQuimio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoJn;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;

public class MedicamentoRNTest extends AGHUBaseUnitTest<MedicamentoRN> {	
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	private static final String EXPECTING_EXCEPTION = "Expecting exception";
	private static final String NOT_EXPECTING_EXCEPTION = "Not expecting exception: ";
	
	@Mock
	IAghuFacade mockedFacade = null;
	@Mock
	MedicamentoRN mockedRn = null;
	@Mock
	private AfaMedicamento mockedMed = null;
	@Mock
	AfaMedicamentoDAO mockedMedDao = null;
	@Mock
	AfaViaAdministracaoMedicamentoDAO mockedViaDao = null;
	@Mock
	AfaFormaDosagemDAO mockedFdDao = null;
	@Mock
	RapServidores mockedRap = null;
	@Mock
	Date date = Calendar.getInstance().getTime();
	private AfaTipoUsoMdto mockedTipoUsoMdto = null;
	@Mock
	private MpmTipoFrequenciaAprazamento mockedTipoFreqApraz = null;
	@Mock
	private AfaTipoApresentacaoMedicamento mockedTipoApres = null;
	@Mock
	private MpmUnidadeMedidaMedica mockedUmm = null;
	
	public class MedicamentoRNWrapper extends MedicamentoRN {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6633262812432771529L;

		public MedicamentoRNWrapper() {
			
			super();
		}		
		
		@Override
		protected boolean desligarValidacaoLocalDispensacao() {

			return false;
		}
		
		@Override
		public IAghuFacade getAghuFacade() {
			
			return MedicamentoRNTest.this.mockedFacade;
		}
		
		@Override
		protected AfaMedicamentoDAO getEntidadeDao() {

			return MedicamentoRNTest.this.mockedMedDao;
		}
		
		@Override
		public Date getDataCriacao() {

			return MedicamentoRNTest.this.date;
		}
		
		@Override
		protected void setServidorData(AfaMedicamento entidade) throws ApplicationBusinessException,
				ApplicationBusinessException {
			MedicamentoRNTest.this.mockedRn.setServidorData(entidade);
		}

		@Override
		protected void setServidorDataJn(AfaMedicamentoJn entidade) throws ApplicationBusinessException,
				ApplicationBusinessException {
			MedicamentoRNTest.this.mockedRn.setServidorDataJn(entidade);
		}
	}

	protected AfaMedicamento getCleanMdtoEntity() {
		
		AfaMedicamento entidade = null;
		String desc = null;
		MpmTipoFrequenciaAprazamento apraz = null;
		AfaTipoUsoMdto tipoUso = null;
		ScoMaterial mat = null;
		DominioSituacaoMedicamento indSit = null;
		DominioIndFotoSensibilidade indFoto = null;
		DominioIndUnidTempoMdto unidTmp = null;
		Short freqUsual = null;
		BigDecimal con = null;
		Short tmpFoto = null;
		DominioQuimio domQui = null;
		AfaComponenteNpt compNpts = null;
		AfaTipoApresentacaoMedicamento tipoApres = null;
		MpmUnidadeMedidaMedica umm = null;
		
		/*
		 * campos
		 */
		entidade = new AfaMedicamento();
		indSit = DominioSituacaoMedicamento.A;
		indFoto = DominioIndFotoSensibilidade.T;
		unidTmp = DominioIndUnidTempoMdto.H;
		freqUsual = Short.valueOf((short)1);
		con = BigDecimal.valueOf(1L);
		tmpFoto = Short.valueOf((short)1);
		domQui = DominioQuimio.B;
		// tipo uso
		tipoUso = new AfaTipoUsoMdto();
		tipoUso.setIndSituacao(DominioSituacao.A);
		//compNpts
		compNpts = new AfaComponenteNpt();
		compNpts.setAfaMedicamentos(entidade);
		//apraz
		apraz = new MpmTipoFrequenciaAprazamento();
		apraz.setIndSituacao(DominioSituacao.A);
		apraz.setIndDigitaFrequencia(Boolean.TRUE);
		//tipo apresentacao
		tipoApres = new AfaTipoApresentacaoMedicamento();
		tipoApres.setSituacao(DominioSituacao.A);
		//umm
		umm = new MpmUnidadeMedidaMedica();
		umm.setIndSituacao(DominioSituacao.A);
		umm.setIndConcentracao(DominioSimNao.S);
		/*
		 * setup
		 */
		entidade.setAfaTipoUsoMdtos(tipoUso);
		entidade.setMpmTipoFreqAprazamentos(apraz);
		entidade.setTipoApresentacaoMedicamento(tipoApres);
		entidade.setScoMaterial(mat);
		entidade.setDescricao(desc);
		entidade.setIndSituacao(indSit);
		entidade.setIndCalcDispensacaoFracionad(Boolean.FALSE);
		entidade.setIndDiluente(Boolean.FALSE);
		entidade.setIndExigeObservacao(Boolean.FALSE);
		entidade.setIndFotosensibilidade(indFoto);
		entidade.setIndGeladeira(Boolean.FALSE);
		entidade.setIndGeraDispensacao(Boolean.FALSE);
		entidade.setIndInteresseCcih(Boolean.FALSE);
		entidade.setIndPadronizacao(Boolean.FALSE);
		entidade.setIndPermiteDoseFracionada(Boolean.FALSE);
		entidade.setIndRevisaoCadastro(Boolean.FALSE);
		entidade.setIndSobraReaproveitavel(Boolean.FALSE);
		entidade.setIndUnidadeTempo(unidTmp);
		entidade.setFrequenciaUsual(freqUsual);
		entidade.setConcentracao(con);
		entidade.setTempoFotosensibilidade(tmpFoto);
		entidade.setTipoQuimio(domQui);
		entidade.setAfaComponenteNpt(compNpts);
		entidade.setMpmUnidadeMedidaMedicas(umm);
		entidade.setAfaFormaDosagens(new HashSet<AfaFormaDosagem>());
		entidade.setViasAdministracaoMedicamento(new HashSet<AfaViaAdministracaoMedicamento>());
		
		return entidade;
	}

	public static MedicamentoRN getMedRNCorretaBri(final AfaMedicamento entidade) {
		
		return new MedicamentoRN() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -8781422305355299809L;
			
			@Override
			protected void setServidorData(AfaMedicamento en) throws ApplicationBusinessException,
					ApplicationBusinessException {
				Assert.assertEquals(entidade, en);
			}
			
			@Override
			protected boolean aplicarAsiPosInsercaoStatement(
					AfaMedicamento entidade) throws BaseException {

				return true;
			}
		};
	}

	@Test
	public void testBriPreInsercaoRowLogic() {
		
		MedicamentoRN objRN = null;
		AfaMedicamento entidade = null;

		// correto
		entidade = this.getCleanMdtoEntity();
		objRN = MedicamentoRNTest.getMedRNCorretaBri(entidade);
		try {
			Assert.assertTrue(objRN.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date()));
		} catch (Exception e) {
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
		// tipo uso inativo
		entidade = this.getCleanMdtoEntity();
		entidade.getAfaTipoUsoMdtos().setIndSituacao(DominioSituacao.I);
		objRN = MedicamentoRNTest.getMedRNCorretaBri(entidade);
		try {
			objRN.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00250, e.getCode());
		}
		// aprazamento inativo
		entidade = this.getCleanMdtoEntity();
		entidade.getMpmTipoFreqAprazamentos().setIndSituacao(DominioSituacao.I);
		objRN = MedicamentoRNTest.getMedRNCorretaBri(entidade);
		try {
			objRN.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00246, e.getCode());
		}
		// aprazamento frequencia inconsistente A
		entidade = this.getCleanMdtoEntity();
		entidade.setFrequenciaUsual(Short.valueOf((short)1));
		entidade.getMpmTipoFreqAprazamentos().setIndDigitaFrequencia(Boolean.FALSE);
		objRN = MedicamentoRNTest.getMedRNCorretaBri(entidade);
		try {
			objRN.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00248, e.getCode());
		}
		// aprazamento frequencia inconsistente B
		entidade = this.getCleanMdtoEntity();
		entidade.setFrequenciaUsual(null);
		entidade.getMpmTipoFreqAprazamentos().setIndDigitaFrequencia(Boolean.TRUE);
		objRN = MedicamentoRNTest.getMedRNCorretaBri(entidade);
		try {
			objRN.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00249, e.getCode());
		}
		// apresentacao inativa
		entidade = this.getCleanMdtoEntity();
		entidade.getTipoApresentacaoMedicamento().setSituacao(DominioSituacao.I);
		objRN = MedicamentoRNTest.getMedRNCorretaBri(entidade);
		try {
			objRN.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00237, e.getCode());
		}
		// umm inativo e concentracao == 's'
		entidade = this.getCleanMdtoEntity();
		entidade.getMpmUnidadeMedidaMedicas().setIndSituacao(DominioSituacao.I);
		entidade.getMpmUnidadeMedidaMedicas().setIndConcentracao(DominioSimNao.S);
		objRN = MedicamentoRNTest.getMedRNCorretaBri(entidade);
		try {
			objRN.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00233, e.getCode());
		}
		// umm ativo e concentracao != 's'
		entidade = this.getCleanMdtoEntity();
		entidade.getMpmUnidadeMedidaMedicas().setIndSituacao(DominioSituacao.A);
		entidade.getMpmUnidadeMedidaMedicas().setIndConcentracao(DominioSimNao.N);
		objRN = MedicamentoRNTest.getMedRNCorretaBri(entidade);
		try {
			objRN.briPreInsercaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00234, e.getCode());
		}
	}

	public static MedicamentoRN getMedRNCorretaAsi(
			final List<AfaViaAdministracaoMedicamento> listaViaAdm, 
			final List<AghUnidadesFuncionais> listaFaltaUnF, 
			final List<AfaFormaDosagem> listaFD, 
			final boolean atualizaFD) {
		
		MedicamentoRN result = null;
		
		result = new MedicamentoRN() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 6296611290311594187L;

			@Override
			protected boolean desligarValidacaoLocalDispensacao() {
				
				return false;
			}
			
			@Override
			protected void setServidorData(AfaMedicamento entidade) throws ApplicationBusinessException,
					ApplicationBusinessException {
				//do nothing
			}
			
			@Override
			protected void setServidorDataJn(AfaMedicamentoJn entidade) throws ApplicationBusinessException,
					ApplicationBusinessException {
				//do nothing
			}
			
			protected List<AfaViaAdministracaoMedicamento> obterListaViaAdministracaoAtiva(AfaMedicamento entidade) {

				return listaViaAdm;
			}
			
			protected List<AghUnidadesFuncionais> obterListaLocalDispensacaoFaltante(AfaMedicamento entidade) {
				
				return listaFaltaUnF;
			}
			
			protected List<AfaFormaDosagem> obterListaFormaDosagemAtivas(AfaMedicamento entidade) {
				
				return listaFD;
			}
			
			protected FormaDosagemON getFormaDosagemON() {
				
				FormaDosagemON result = null;
				
				result = new FormaDosagemON(){
					
					/**
					 * 
					 */
					private static final long serialVersionUID = -8365547613059743621L;

					public void inserir(AfaFormaDosagem entidade) throws IllegalStateException ,BaseException {
						
						if (!atualizaFD) {
							throw new IllegalArgumentException();
						}
					}
				};
				
				return result;
			}
		};
		
		return result;
	}

	@Test
	public void testAplicarAsiPosInsercaoStatement() {
		
		MedicamentoRN objRN = null;
		AfaMedicamento entidade = null;
		List<AfaViaAdministracaoMedicamento> listaViaAdm = null;
		List<AghUnidadesFuncionais> listaFaltaUnF = null;
		List<AfaFormaDosagem> listaFD = null;
		AfaViaAdministracaoMedicamento viaAdm = null;
		AghUnidadesFuncionais unF = null;
		AfaFormaDosagem fd = null;
		boolean atualizaFD = false;

		/*
		 * Parametros
		 */
		// via adm
		listaViaAdm = new LinkedList<AfaViaAdministracaoMedicamento>();
		viaAdm = new AfaViaAdministracaoMedicamento();
		listaViaAdm.add(viaAdm);
		// unid. func.
		listaFaltaUnF = new LinkedList<AghUnidadesFuncionais>();
		unF = new AghUnidadesFuncionais();
		listaFaltaUnF.add(unF);
		// forma dosagem
		listaFD = new LinkedList<AfaFormaDosagem>();
		fd = new AfaFormaDosagem();
		listaFD.add(fd);
		// atualiza
		atualizaFD = true;					
		/*
		 * Execucao
		 */
		// correto inativo
		objRN = MedicamentoRNTest.getMedRNCorretaAsi(listaViaAdm, null, listaFD, atualizaFD);
		entidade = this.getCleanMdtoEntity();
		entidade.setIndSituacao(DominioSituacaoMedicamento.I);
		try {
			Assert.assertTrue(objRN.aplicarAsiPosInsercaoStatement(entidade));
		} catch (BaseException e) {
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
		// correto ativo
		objRN = MedicamentoRNTest.getMedRNCorretaAsi(listaViaAdm, null, listaFD, atualizaFD);
		entidade = this.getCleanMdtoEntity();
		entidade.setIndSituacao(DominioSituacaoMedicamento.A);
		try {
			Assert.assertTrue(objRN.aplicarAsiPosInsercaoStatement(entidade));
		} catch (BaseException e) {
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
		// incorreto: sem via adm ativa
		objRN = MedicamentoRNTest.getMedRNCorretaAsi(null, null, listaFD, atualizaFD);
		entidade = this.getCleanMdtoEntity();
		entidade.setIndSituacao(DominioSituacaoMedicamento.A);
		try {
			objRN.aplicarAsiPosInsercaoStatement(entidade);
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00251, e.getCode());
		}
		// incorreto: sem definicao completa de local de dispensacao
		objRN = MedicamentoRNTest.getMedRNCorretaAsi(listaViaAdm, listaFaltaUnF, listaFD, atualizaFD);
		entidade = this.getCleanMdtoEntity();
		entidade.setIndSituacao(DominioSituacaoMedicamento.A);
		try {
			objRN.aplicarAsiPosInsercaoStatement(entidade);
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00229, e.getCode());
		}
		// incorreto: sem forma de dosagem ativa
		objRN = MedicamentoRNTest.getMedRNCorretaAsi(listaViaAdm, null, null, atualizaFD);
		entidade = this.getCleanMdtoEntity();
		entidade.setIndSituacao(DominioSituacaoMedicamento.A);
		try {
			objRN.aplicarAsiPosInsercaoStatement(entidade);
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00227, e.getCode());
		}
		// incorreto: erro cadastrando forma de dosagem
		objRN = MedicamentoRNTest.getMedRNCorretaAsi(listaViaAdm, null, listaFD, !atualizaFD);
		entidade = this.getCleanMdtoEntity();
		entidade.setIndSituacao(DominioSituacaoMedicamento.A);
		try {
			objRN.aplicarAsiPosInsercaoStatement(entidade);
			objRN.asiPosInsercaoStatement(entidade, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00266, e.getCode());
		}
	}

	protected MedicamentoRN getMedRNCorretaBru(final List<MpmItemPrescricaoMdto> listaItemPrescMdto, final Date curr) {
		
		return new MedicamentoRN() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -4748303826823333538L;

			@Override
			protected java.util.List<MpmItemPrescricaoMdto> obterListaItemPrescricaoParaMdto(AfaMedicamento entidade) {
				
				return listaItemPrescMdto;
			}
						
			@Override
			public Date getDataCriacao() {
				
				return curr;
			}
			
			@Override
			protected void setServidorData(AfaMedicamento entidade) throws ApplicationBusinessException,
					ApplicationBusinessException {
				//do nothing
			}
			
			@Override
			protected boolean aplicarAruPosAtualizacaoRow(AfaMedicamento original, AfaMedicamento modificada, String nomeMicrocomputador) throws BaseException {
				
				return true;
			}
			
			@Override
			protected boolean aplicarAsuPosAtualizacaoStatement(AfaMedicamento original, AfaMedicamento modificada) throws BaseException {
				
				return true;
			}
		};
	}

	@Test
	public void testBruPreAtualizacaoRow() {
		
		MedicamentoRN objRN = null;
		AfaMedicamento original = null;
		AfaMedicamento modificada = null;
		final Date curr = new Date(10000L);
		final Date fut = new Date(20000L);
		MpmPrescricaoMdto presc = null;
		List<MpmItemPrescricaoMdto> listaItemPrescMdto = null;
		MpmItemPrescricaoMdto itemPresc = null;
		AghAtendimentos atend = null;
		AfaFormaDosagem fd = null;
		BigDecimal fatorUp = null;
		double valFatorUp = 1.0d;

		// correto
		listaItemPrescMdto = Collections.emptyList();
		objRN = this.getMedRNCorretaBru(listaItemPrescMdto, curr);
		original = this.getCleanMdtoEntity();
		modificada = this.getCleanMdtoEntity();
		try {
			Assert.assertTrue(objRN.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date()));
		} catch (BaseException e) {
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
		// correto: mudanca de dose fracionaria para nao-fracionaria, mas sem nenhum atendimento pendente
		listaItemPrescMdto = Collections.emptyList();
		objRN = this.getMedRNCorretaBru(listaItemPrescMdto, curr);
		original = this.getCleanMdtoEntity();
		modificada = this.getCleanMdtoEntity();
		original.setIndPermiteDoseFracionada(Boolean.TRUE);
		modificada.setIndPermiteDoseFracionada(Boolean.FALSE);
		try {
			Assert.assertTrue(objRN.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date()));
		} catch (BaseException e) {
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
		// correto: mudanca de dose fracionaria para nao-fracionaria, mas COM atendimento pendente, mas sem dose frac.
		// atendimento
		atend = new AghAtendimentos();
		atend.setIndPacAtendimento(DominioPacAtendimento.S);
		atend.setProntuario(Integer.valueOf(123));
		// fator conversao
		valFatorUp = 1.0d;
		fatorUp = new BigDecimal(valFatorUp);		
		// forma dosagem
		fd = new AfaFormaDosagem();
		fd.setFatorConversaoUp(fatorUp);
		// prescricao
		presc = new MpmPrescricaoMdto();
		presc.setDthrFim(fut);
		presc.setPrescricaoMedica(new MpmPrescricaoMedica());
		presc.getPrescricaoMedica().setAtendimento(atend);		
		
		presc.setItensPrescricaoMdtos(listaItemPrescMdto);
		// item presc.
		itemPresc = new MpmItemPrescricaoMdto();
		itemPresc.setFormaDosagem(fd);
		itemPresc.setPrescricaoMedicamento(presc);
		// lista item presc.
		listaItemPrescMdto = new LinkedList<MpmItemPrescricaoMdto>();
		listaItemPrescMdto.add(itemPresc);
		// marca a entidade original com estas prescricoes		
		objRN = this.getMedRNCorretaBru(listaItemPrescMdto, curr);
		original = this.getCleanMdtoEntity();
		modificada = this.getCleanMdtoEntity();
		original.setIndPermiteDoseFracionada(Boolean.TRUE);
		modificada.setIndPermiteDoseFracionada(Boolean.FALSE);
		try {
			Assert.assertTrue(objRN.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date()));
		} catch (BaseException e) {
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
		// A: incorreto: mudanca de dose fracionaria para nao-fracionaria, mas COM atendimento pendente, mas COM dose frac.
		// fator conversao
		valFatorUp = 1.2d;
		fatorUp = new BigDecimal(valFatorUp);
		fd.setFatorConversaoUp(fatorUp);
		objRN = this.getMedRNCorretaBru(listaItemPrescMdto, curr);
		try {
			objRN.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_01221, e.getCode());
		}
		// B: incorreto: mudanca de dose fracionaria para nao-fracionaria, mas COM atendimento pendente, mas COM dose frac.
		// adiciona outro atendimento
		listaItemPrescMdto.add(itemPresc);
		objRN = this.getMedRNCorretaBru(listaItemPrescMdto, curr);
		try {
			objRN.bruPreAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_01222, e.getCode());
		}
	}

	protected MedicamentoRN getMedRNCorretaAru(final AfaMedicamento modificada, final boolean compNptOk, final boolean viaAdmMdtoOk, final boolean formaDosagemOk) {
		
		return new MedicamentoRN() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -1233369658944156435L;

			@Override
			protected ComponenteNptON getComponentNptON() {
				
				return new ComponenteNptON(){
					
					/**
					 * 
					 */
					private static final long serialVersionUID = -7333143233401414216L;

					@Override
					public void atualizar(AfaComponenteNpt entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException ,BaseException {
						
						if (compNptOk) {
							Assert.assertEquals(modificada.getAfaComponenteNpt(), entidade);							
						} else {
							throw new IllegalStateException();
						}
					}
				};
			}
			
			@Override
			protected AfaFormaDosagemDAO getFormaDosagemDao() {

				return MedicamentoRNTest.this.mockedFdDao;
			}
			
			@Override
			protected AfaViaAdministracaoMedicamentoDAO getViaAdmMdtoDao() {
				
				return MedicamentoRNTest.this.mockedViaDao;
			}
						
			@Override
			protected ViaAdministracaoMedicamentoON getViaAdmMdtoON() {
				
				return new ViaAdministracaoMedicamentoON() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = -60916745717572188L;

					@Override
					public void atualizar(AfaViaAdministracaoMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException ,BaseException {
						
						if (viaAdmMdtoOk) {
							Assert.assertTrue(modificada.getViasAdministracaoMedicamento().contains(entidade));							
						} else {
							throw new IllegalStateException();
						}
					}
				};
			}
			
			@Override
			protected FormaDosagemON getFormaDosagemON() {

				return new FormaDosagemON() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 4988158267312666907L;

					@Override
					public void atualizar(AfaFormaDosagem entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
							throws IllegalStateException, BaseException {
						
						if (formaDosagemOk) {							
							Assert.assertTrue(modificada.getAfaFormaDosagens().contains(entidade));							
						} else {
							throw new IllegalStateException();
						}
					}
				};
			}
			
			@Override
			protected void setServidorData(AfaMedicamento entidade) throws ApplicationBusinessException,
					ApplicationBusinessException {
				Assert.assertEquals(modificada, entidade);
			}
			
			@Override
			protected boolean inserirEntradaJournal(AfaMedicamento entidade,
					br.gov.mec.aghu.core.dominio.DominioOperacoesJournal operacao) throws ApplicationBusinessException,
					ApplicationBusinessException {
				return DominioOperacoesJournal.UPD.equals(operacao);
			}
			
		};
	}

	@Test
	public void testAplicarAsuPosAtualizacaoStatement() {
		
		MedicamentoRN objRN = null;
		AfaMedicamento original = null;
		AfaMedicamento modificada = null;
		List<AfaViaAdministracaoMedicamento> listaViaAdm = null;
		List<AghUnidadesFuncionais> listaFaltaUnF = null;
		List<AfaFormaDosagem> listaFD = null;
		AfaViaAdministracaoMedicamento viaAdm = null;
		AghUnidadesFuncionais unF = null;
		AfaFormaDosagem fd = null;
		boolean atualizaFD = false;

		/*
		 * Parametros
		 */
		// via adm
		listaViaAdm = new LinkedList<AfaViaAdministracaoMedicamento>();
		viaAdm = new AfaViaAdministracaoMedicamento();
		listaViaAdm.add(viaAdm);
		// unid. func.
		listaFaltaUnF = new LinkedList<AghUnidadesFuncionais>();
		unF = new AghUnidadesFuncionais();
		listaFaltaUnF.add(unF);
		// forma dosagem
		listaFD = new LinkedList<AfaFormaDosagem>();
		fd = new AfaFormaDosagem();
		listaFD.add(fd);
		// atualiza
		atualizaFD = true;					
		/*
		 * Execucao
		 */
		original = getCleanMdtoEntity();
		modificada = getCleanMdtoEntity();
		original.setIndSituacao(DominioSituacaoMedicamento.I);
		modificada.setIndSituacao(DominioSituacaoMedicamento.A);
		objRN = MedicamentoRNTest.getMedRNCorretaAsi(listaViaAdm, null, listaFD, atualizaFD);
		// correto : de inativo para ativo
		try {
			Assert.assertTrue(objRN.aplicarAsuPosAtualizacaoStatement(original, modificada));
		} catch (BaseException e) {
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
	}

	@Test
	public void testBrdPreRemocaoRow() {
		
		MedicamentoRN objRN = null;
		AfaMedicamento entidade = null;

		entidade = getCleanMdtoEntity();
		objRN = new MedicamentoRN() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -2413382896164376754L;
			
			@Override
			protected boolean aplicarArdPosRemocaoRow(AfaMedicamento entidade) throws BaseException {
				return false;
			}
			
		};
		// correto : entidade
		try {
			objRN.brdPreRemocaoRow(entidade, NOME_MICROCOMPUTADOR, new Date());
			fail(EXPECTING_EXCEPTION);
		} catch (BaseException e) {
			Assert.assertEquals(FarmaciaExceptionCode.AFA_00226, e.getCode());
		}
		// correto : null entidade
		try {
			Assert.assertFalse(objRN.brdPreRemocaoRow(null, NOME_MICROCOMPUTADOR, new Date()));
		} catch (BaseException e) {
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
	}

	@Test
	public void testAplicarArdPosRemocaoRow() {
		
		MedicamentoRN objRN = null;
		AfaMedicamento entidade = null;

		entidade = getCleanMdtoEntity();
		objRN = new MedicamentoRN() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 3802564338786920804L;

			@Override
			protected void setServidorData(AfaMedicamento entidade) throws ApplicationBusinessException,
					ApplicationBusinessException {
				//do nothing
			}
			
			@Override
			protected boolean inserirEntradaJournal(AfaMedicamento entidade,
					br.gov.mec.aghu.core.dominio.DominioOperacoesJournal operacao) throws ApplicationBusinessException,
					ApplicationBusinessException {
				return DominioOperacoesJournal.DEL.equals(operacao);
			}
		};
		
		// correto
		try {
			Assert.assertTrue(objRN.aplicarArdPosRemocaoRow(entidade));
		} catch (BaseException e) {
			fail(NOT_EXPECTING_EXCEPTION + e);
		}
	}

}
